package ru.yandex.practicum.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import ru.yandex.practicum.payment.client.api.DefaultApi;
import ru.yandex.practicum.payment.client.invoker.ApiClient;
import ru.yandex.practicum.paymentservice.config.security.CurrentUserService;

@Configuration
public class PaymentClientConfig {

    @Bean
    public OAuth2AuthorizedClientManager paymentOAuth2AuthorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {
        var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);

        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();
        manager.setAuthorizedClientProvider(authorizedClientProvider);

        return manager;
    }

    @Bean
    public WebClient paymentWebClient(
            OAuth2AuthorizedClientManager authorizedClientManager, CurrentUserService currentUserService) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2.setDefaultClientRegistrationId("payment-service");

        ExchangeFilterFunction userHeader = (request, next) -> {
            String username = currentUserService.requireCurrentUser().getUsername();
            return next.exchange(org.springframework.web.reactive.function.client.ClientRequest.from(request)
                    .header("X-User-Name", username)
                    .build());
        };

        return WebClient.builder()
                .filter(userHeader)
                .apply(oauth2.oauth2Configuration())
                .build();
    }

    @Bean
    public ApiClient paymentApiClient(WebClient paymentWebClient) {
        ApiClient apiClient = new ApiClient(paymentWebClient);
        apiClient.setBasePath("http://localhost:8111");
        return apiClient;
    }

    @Bean
    public DefaultApi paymentApi(ApiClient apiClient) {
        return new DefaultApi(apiClient);
    }
}
