# Microservices Architecture with Spring Boot & Spring Cloud

## Overview

This project demonstrates a complete microservices architecture built
with Spring Boot and Spring Cloud.\
It was designed as a hands-on learning project to understand service
discovery, API Gateway routing, load balancing, and inter-service
communication.

The architecture follows modern distributed system principles and
simulates a production-like environment locally.

------------------------------------------------------------------------

## Architecture Components

The system is composed of four independent services:

-   **eureka-server** → Service Discovery
-   **api-gateway** → Centralized Routing
-   **user-service** → User management (H2 database)
-   **order-service** → Order management with inter-service
    communication

------------------------------------------------------------------------

## Architecture Summary

  Component        Responsibility
  ---------------- --------------------------------------------------------
  Eureka           Service Discovery
  API Gateway      Centralized routing
  lb://            Client-side load balancing
  Local Cache      Fault tolerance when Eureka is temporarily unavailable
  server.port: 0   Horizontal scalability simulation

------------------------------------------------------------------------

## Technology Stack

-   Java 17
-   Spring Boot 3.x
-   Spring Cloud 2023.x
-   Eureka Server
-   Spring Cloud Gateway
-   OpenFeign
-   Spring Data JPA
-   H2 Database
-   Spring Cloud LoadBalancer

------------------------------------------------------------------------

# Services Description

## 1. Eureka Server

### Purpose

Acts as the Service Registry where all microservices register
themselves.

### Dependencies

-   spring-boot-starter-web
-   spring-cloud-starter-netflix-eureka-server

### Configuration

``` yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

Access Dashboard:

    http://localhost:8761

------------------------------------------------------------------------

## 2. User Service

### Purpose

Handles user CRUD operations using an in-memory H2 database.

### Dependencies

-   spring-boot-starter-web
-   spring-boot-starter-data-jpa
-   h2
-   spring-cloud-starter-netflix-eureka-client
-   spring-cloud-starter-openfeign

### Key Configuration

``` yaml
server:
  port: 0

spring:
  application:
    name: user-service
```

Using `server.port: 0` allows multiple instances to run simultaneously,
enabling horizontal scaling simulation.

------------------------------------------------------------------------

## 3. Order Service

### Purpose

Creates orders and communicates with the User Service using OpenFeign.

### Dependencies

-   spring-boot-starter-web
-   spring-boot-starter-data-jpa
-   h2
-   spring-cloud-starter-netflix-eureka-client
-   spring-cloud-starter-openfeign

### Inter-service Communication

``` java
@FeignClient(name = "user-service")
```

The service dynamically discovers user-service instances via Eureka.

------------------------------------------------------------------------

## 4. API Gateway

### Purpose

Single entry point for all client requests. Routes traffic dynamically
using service discovery.

### Dependencies

-   spring-cloud-starter-gateway
-   spring-cloud-starter-netflix-eureka-client

### Routing Configuration

``` yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/users/**

        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/orders/**
```

The `lb://` protocol enables automatic load balancing across service
instances.

------------------------------------------------------------------------

# Request Flow

Client → API Gateway → Eureka → Target Microservice

1.  Client calls API Gateway
2.  Gateway consults Eureka
3.  Eureka returns available instances
4.  LoadBalancer selects an instance
5.  Request is forwarded

------------------------------------------------------------------------

# How to Run the Project

### 1. Start Eureka Server

Run the `eureka-server` application.

### 2. Start User Service

Run the `user-service` application.

### 3. Start Order Service

Run the `order-service` application.

### 4. Start API Gateway

Run the `api-gateway` application.

------------------------------------------------------------------------

# Access Endpoints (via Gateway)

    http://localhost:8080/users
    http://localhost:8080/orders

------------------------------------------------------------------------

# Concepts Demonstrated

-   Service Discovery
-   API Gateway Pattern
-   Client-side Load Balancing
-   Horizontal Scaling Simulation
-   Inter-service Communication (Feign)
-   Fault Tolerance via Local Registry Cache
-   In-memory Database (H2)
-   Distributed Architecture Fundamentals

------------------------------------------------------------------------

# Future Improvements

-   JWT Authentication with Spring Security
-   Circuit Breaker (Resilience4j)
-   Config Server
-   Docker & Docker Compose
-   Observability (Zipkin / Micrometer)
-   PostgreSQL integration
-   Kubernetes deployment
-   Integration & Unit Testing

------------------------------------------------------------------------

## Author

Project created for learning and portfolio purposes to demonstrate
understanding of modern microservices architecture using Spring
ecosystem.
