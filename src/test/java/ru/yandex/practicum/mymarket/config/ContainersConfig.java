package ru.yandex.practicum.mymarket.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

    public interface ContainersConfig {

        @ServiceConnection
        PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("market-test")
            .withUsername("postgres")
            .withPassword("postgres");
    }