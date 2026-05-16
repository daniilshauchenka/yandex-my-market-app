package ru.yandex.practicum.paymentservice.util;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

import ru.yandex.practicum.paymentservice.config.ContainersConfig;

@SpringBootTest
@AutoConfigureMockMvc
@ImportTestcontainers(ContainersConfig.class)
public abstract class AbstractIntegrationTest {}
