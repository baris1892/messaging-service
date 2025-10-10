package dev.baristop.portfolio.messaging_service.kafka;

import dev.baristop.portfolio.messaging_service.kafka.dto.ListingStatusChangedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    public static final String TOPIC_LISTING_STATUS_CHANGED = "listing-status-changed";
    public static final String TOPIC_LISTING_STATUS_CHANGED_DLT = "listing-status-changed.DLT";

    @Bean
    public NewTopic listingStatusChangedDltTopic() {
        return TopicBuilder.name(TOPIC_LISTING_STATUS_CHANGED_DLT)
            .partitions(1)
            .replicas(1)
            .build();
    }

    @Bean
    public ConsumerFactory<String, ListingStatusChangedEvent> listingStatusChangedConsumerFactory(
        @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
        @Value("${spring.kafka.consumer.group-id}") String groupId) {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            new ErrorHandlingDeserializer<>(
                new JsonDeserializer<>(ListingStatusChangedEvent.class, false)
            )
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ListingStatusChangedEvent> listingStatusChangedKafkaListenerContainerFactory(
        ConsumerFactory<String, ListingStatusChangedEvent> listingStatusChangedConsumerFactory,
        @Qualifier("dltKafkaTemplate") KafkaTemplate<Object, Object> dltKafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, ListingStatusChangedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(listingStatusChangedConsumerFactory);

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
            dltKafkaTemplate,
            (record, ex) -> {
                String dltTopic = record.topic() + ".DLT";
                int partition = record.partition();
                // log full exception
                // log.error("Will publish to DLT topic '{}' partition {} because: {}", dltTopic, partition, ex.toString(), ex);

                // log partial exception
                log.error("Will publish to DLT topic '{}' partition {} because: {}", dltTopic, partition, ex.toString());

                return new TopicPartition(dltTopic, partition);
            }
        );

        // Exponential backoff: retry 3 times, 1s -> 2s -> 4s
        ExponentialBackOffWithMaxRetries backoff = new ExponentialBackOffWithMaxRetries(3);
        backoff.setInitialInterval(1000L);
        backoff.setMultiplier(2.0);
        backoff.setMaxInterval(8000L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backoff);

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) ->
            log.warn("Retry attempt {} for record {} failed: {}", deliveryAttempt, record, ex.getMessage())
        );

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public ProducerFactory<Object, Object> dltProducerFactory(
        @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers
    ) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Optional: configure additional producer props (acks, retries, linger.ms, etc)
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<Object, Object> dltKafkaTemplate(
        ProducerFactory<Object, Object> dltProducerFactory
    ) {
        return new KafkaTemplate<>(dltProducerFactory);
    }
}
