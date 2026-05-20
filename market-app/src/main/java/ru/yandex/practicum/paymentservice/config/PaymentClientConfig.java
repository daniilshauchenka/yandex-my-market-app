package ru.yandex.practicum.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.yandex.practicum.payment.client.api.DefaultApi;
import ru.yandex.practicum.payment.client.invoker.ApiClient;

@Configuration
public class PaymentClientConfig {

    @Bean
    public ApiClient paymentApiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("http://localhost:8111");
        return apiClient;
    }

    @Bean
    public DefaultApi paymentApi(ApiClient apiClient) {
        return new DefaultApi(apiClient);
    }
}
