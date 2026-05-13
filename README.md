# MyMarket

Online store application built with Spring Boot.

## Tech Stack
- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- PostgreSQL
- Thymeleaf
- Testcontainers
- JUnit 5
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
* MockMvc

### 4. Features
* Browse products
* Product search
* Product sorting
* Add items to cart
* Change item quantity 
* Create orders
* View orders
* Global exception handling