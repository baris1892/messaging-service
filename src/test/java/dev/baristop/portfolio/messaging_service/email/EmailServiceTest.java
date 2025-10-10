package dev.baristop.portfolio.messaging_service.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    private static final String RECIPIENT_EMAIL = "user@example.com";
    private static final String SUBJECT = "Test Subject";
    private static final String BODY = "Hello World";
    private static final String FROM = "App <no-reply@app.com>";

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
    }

    @Test
    void shouldSendPlainTextEmail() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendPlainTextEmail(RECIPIENT_EMAIL, SUBJECT, BODY);

        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();

        assertThat(message.getTo()).containsExactly(RECIPIENT_EMAIL);
        assertThat(message.getSubject()).isEqualTo(SUBJECT);
        assertThat(message.getText()).isEqualTo(BODY);
        assertThat(message.getFrom()).isEqualTo(FROM);
    }
}
