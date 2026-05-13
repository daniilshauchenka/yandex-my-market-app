# MyMarket

Reactive online store application built with Spring Boot and Spring WebFlux.

## Tech Stack
- Java 21
- Spring Boot
- Spring WebFlux
- Spring Data R2DBC
- PostgreSQL
- Thymeleaf
- Reactor
- Testcontainers
- JUnit 5
- WebTestClient
- Docker

---

## Run the Application

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