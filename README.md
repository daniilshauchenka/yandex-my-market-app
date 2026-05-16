# MyMarket

Online store application built with Spring Boot and a separate reactive payment service.

## Tech Stack
- Java 21
- Spring Boot 3
- Spring MVC
- Spring WebFlux
- Spring Data JPA
- Spring Data Redis
- PostgreSQL
- Redis
- OpenAPI Generator
- Thymeleaf
- Liquibase
- Testcontainers
- JUnit 5
- Docker
- Docker Compose

---

## Run the ru.yandex.practicum.paymentservice.Application

### 1. Build the project

```bash
mvn clean install
```

### 2. Run with docker compose
```bash
docker compose up --build
```

### 3. Tests
```bash
mvn test
```
#### Integration tests use:

* Testcontainers
* PostgreSQL container
* Redis
* MockMvc
* WebTestClient

### 4. Features
* Browse products
* Product search
* Product sorting
* Add items to cart
* Change item quantity 
* Create orders
* View orders
* Global exception handling

### 5. OpenAPI
Client and server code are generated from:
```
openapi/payment-api.yaml
```
Swagger:
```
http://localhost:8111/swagger-ui.html
```