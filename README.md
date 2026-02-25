# Arquitetura de Microserviços com Spring Boot e Spring Cloud

## 📌 Visão Geral

Este projeto demonstra uma arquitetura completa de microserviços
utilizando Spring Boot e Spring Cloud.

Foi desenvolvido com foco em aprendizado prático, simulando um ambiente
próximo ao utilizado em produção, incluindo:

-   Service Discovery
-   API Gateway
-   Load Balancing
-   Comunicação entre microsserviços
-   Escalabilidade horizontal
-   Banco de dados em memória (H2)

O objetivo é demonstrar domínio dos fundamentos de arquitetura
distribuída utilizando o ecossistema Spring.

------------------------------------------------------------------------

## 🏗️ Arquitetura do Sistema

Estratégia de autenticação escolhida: **Opção B (self-hosted)** com um
novo `auth-service` baseado em Spring Authorization Server.

O sistema é composto por cinco aplicações independentes:

-   **eureka-server** → Service Discovery
-   **api-gateway** → Roteamento centralizado
-   **user-service** → Serviço de usuários (H2)
-   **order-service** → Serviço de pedidos com comunicação entre
    serviços
-   **auth-service** → Authorization Server OAuth2/OpenID Connect

------------------------------------------------------------------------

## 🧠 Resumo de Arquiteto

  Componente       Função
  ---------------- ----------------------------------------
  Eureka           Service Discovery
  Gateway          Roteamento centralizado
  lb://            Load balancing automático
  Cache local      Tolerância a falhas
  server.port: 0   Simulação de escalabilidade horizontal

------------------------------------------------------------------------

## 🛠️ Tecnologias Utilizadas

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

# 📦 Descrição dos Serviços

## 1️⃣ Eureka Server

### 🎯 Responsabilidade

Atua como Service Registry. Todos os microsserviços se registram nele
para que possam ser descobertos dinamicamente.

### 📦 Dependências

-   spring-boot-starter-web
-   spring-cloud-starter-netflix-eureka-server

### ⚙️ Configuração

``` yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

Acessar dashboard:

    http://localhost:8761

------------------------------------------------------------------------

## 2️⃣ User Service

### 🎯 Responsabilidade

Gerencia operações de usuários (CRUD básico) utilizando banco H2 em
memória.

### 📦 Dependências

-   spring-boot-starter-web
-   spring-boot-starter-data-jpa
-   h2
-   spring-cloud-starter-netflix-eureka-client
-   spring-cloud-starter-openfeign

### ⚙️ Configuração Principal

``` yaml
server:
  port: 0

spring:
  application:
    name: user-service
```

A configuração `server.port: 0` permite que múltiplas instâncias rodem
simultaneamente, simulando escalabilidade horizontal.

------------------------------------------------------------------------

## 3️⃣ Order Service

### 🎯 Responsabilidade

Cria pedidos e realiza comunicação com o User Service via OpenFeign.

### 📦 Dependências

-   spring-boot-starter-web
-   spring-boot-starter-data-jpa
-   h2
-   spring-cloud-starter-netflix-eureka-client
-   spring-cloud-starter-openfeign

### 🔄 Comunicação entre serviços

``` java
@FeignClient(name = "user-service")
```

O serviço descobre dinamicamente as instâncias disponíveis através do
Eureka.

------------------------------------------------------------------------

## 4️⃣ API Gateway

### 🎯 Responsabilidade

Ponto único de entrada do sistema. Responsável por rotear requisições
para os microsserviços corretos.

### 📦 Dependências

-   spring-cloud-starter-gateway
-   spring-cloud-starter-netflix-eureka-client

### ⚙️ Configuração de Rotas

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

O prefixo `lb://` ativa o LoadBalancer automaticamente utilizando o
registry do Eureka.

------------------------------------------------------------------------

## 5️⃣ Auth Service

### 🎯 Responsabilidade

Atua como Authorization Server central do ecossistema, emitindo JWTs
assinados por chave RSA e disponibilizando endpoints OAuth2 padrão.

### 📦 Dependências

-   spring-boot-starter-oauth2-authorization-server
-   spring-boot-starter-security
-   spring-cloud-starter-netflix-eureka-client

### 👥 Clientes OAuth2 configurados

-   **gateway-client**
    - grant: `client_credentials`
    - autenticação: `client_secret_basic`
    - scopes: `users.read`, `orders.read`

-   **swagger-client**
    - grants: `authorization_code`, `refresh_token`
    - autenticação: `client_secret_basic`
    - redirect URIs:
        - `http://127.0.0.1:8080/login/oauth2/code/swagger`
        - `http://localhost:8080/swagger-ui/oauth2-redirect.html`
    - scopes: `openid`, `profile`, `users.read`

### 🔐 Fluxos suportados

-   `client_credentials` (integração serviço a serviço, sem usuário)
-   `authorization_code` (aplicações com login de usuário)
-   `refresh_token` (renovação de sessão)

### 🌐 Endpoints OAuth2 padrão

-   `POST /oauth2/token`
-   `GET /oauth2/authorize`
-   `GET /oauth2/jwks`
-   `GET /.well-known/oauth-authorization-server`

### ⚙️ Execução

-   Porta padrão: `9000`
-   Issuer: `http://localhost:9000`
-   Registro no Eureka habilitado para descoberta interna

------------------------------------------------------------------------

# 🔄 Fluxo de Requisição

Cliente → API Gateway → Eureka → Microsserviço alvo

1.  Cliente envia requisição ao Gateway
2.  Gateway consulta o Eureka
3.  Eureka retorna as instâncias disponíveis
4.  LoadBalancer seleciona uma instância
5.  Requisição é encaminhada

------------------------------------------------------------------------

# 🚀 Como Executar o Projeto

### 1️⃣ Subir o Eureka Server

Executar a aplicação `eureka-server`.

### 2️⃣ Subir o User Service

Executar a aplicação `user-service`.

### 3️⃣ Subir o Order Service

Executar a aplicação `order-service`.

### 4️⃣ Subir o API Gateway

Executar a aplicação `api-gateway`.

### 5️⃣ Subir o Auth Service

Executar a aplicação `auth-service`.

------------------------------------------------------------------------

# 🌍 Acessar os Endpoints (via Gateway)

    http://localhost:8080/users
    http://localhost:8080/orders

------------------------------------------------------------------------

# 📚 Conceitos Demonstrados

-   Service Discovery
-   Padrão API Gateway
-   Load Balancing Client-Side
-   Escalabilidade Horizontal
-   Comunicação entre Microsserviços
-   Tolerância a Falhas via Cache Local
-   Banco em memória (H2)
-   Fundamentos de Arquitetura Distribuída

------------------------------------------------------------------------

# 🚀 Próximas Evoluções

-   Autenticação com JWT + Spring Security
-   Circuit Breaker (Resilience4j)
-   Config Server
-   Docker e Docker Compose
-   Observabilidade (Zipkin / Micrometer)
-   Integração com PostgreSQL
-   Deploy em Kubernetes
-   Testes automatizados

------------------------------------------------------------------------

## 👨‍💻 Autor

Projeto desenvolvido para fins educacionais e portfólio, demonstrando
conhecimento prático em arquitetura de microserviços com o ecossistema
Spring.
