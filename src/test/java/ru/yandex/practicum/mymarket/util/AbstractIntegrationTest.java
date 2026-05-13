 package ru.yandex.practicum.mymarket.util;

 import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
 import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.boot.testcontainers.context.ImportTestcontainers;
 import ru.yandex.practicum.mymarket.config.ContainersConfig;

 @SpringBootTest
 @AutoConfigureWebTestClient
 @ImportTestcontainers(ContainersConfig.class)
 public abstract class AbstractIntegrationTest {
 }