package ru.yandex.practicum.paymentservice.util;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.paymentservice.config.ContainersConfig;
import ru.yandex.practicum.paymentservice.config.TestOAuth2ClientConfig;
import ru.yandex.practicum.paymentservice.service.impl.PaymentGatewayService;

@SpringBootTest
@AutoConfigureMockMvc
@ImportTestcontainers(ContainersConfig.class)
@Import(TestOAuth2ClientConfig.class)
public abstract class AbstractIntegrationTest {

    @MockitoBean
    protected PaymentGatewayService paymentGatewayService;
}
