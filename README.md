# Messaging Service

> ⚠️ **Portfolio / Demo** — This repository is part of a personal portfolio

![GitHub Actions](https://github.com/baris-top-portfolio/messaging-service/workflows/Messaging%20Service%20CI/CD/badge.svg)

Spring Boot microservice that **consumes Kafka events** (e.g., `ListingStatusChangedEvent`) and sends email
notifications.
It includes **Dead Letter Topic (DLT) handling** for failed messages and automatic retries.

## 🛠️ Technology Stack

- **Spring Boot 3** – microservice framework
- **Kafka** – event-driven messaging, including DLT
- **Mailpit** – local email testing
- **JUnit + Mockito** – unit & integration testing

## ⚡ Features

- Consume Kafka events published by [Listing Service](https://github.com/baris-top-portfolio/listing-service)
- Send email notifications (Mailpit for local testing)
- Automatic DLT handling and retries with **exponential backoff**

Note: For infrastructure overview (Kafka, Redis, Keycloak, PostgreSQL), see
the [Listing Service](https://github.com/baris-top-portfolio/listing-service) README

## 🔄 CI/CD

This project uses **GitHub Actions** for continuous integration (and deployment):

- **Build Job:** Compiles the application and creates a JAR artifact
- **Tests Job:** Runs automated Maven tests with test profile
- **Docker Job:** Builds and pushes Docker images for release tags
- **Deploy Job:** (Dummy for local k3s) Simulates deployment to local Kubernetes cluster

For more details, check `.github/workflows/ci-cd.yaml`.

## 📥 Installation & Local Setup

For installation, see the separate [INSTALL.md](./INSTALL.md). It includes:

- Environment variable setup (.env.dist)
- Starting required dependencies (Kafka, Mailpit)
- Running the service locally with Maven
