package ru.yandex.practicum.paymentservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import ru.yandex.practicum.payment.model.PaymentResponse;
import ru.yandex.practicum.paymentservice.service.PaymentService;

import reactor.core.publisher.Mono;

@WebFluxTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void shouldReturnBalance() {
        when(paymentService.getBalance(eq("buyer"))).thenReturn(Mono.just(BigDecimal.valueOf(5000)));
        webTestClient
                .mutateWith(mockJwt())
                .get()
                .uri("/payments/balance")
                .header("X-User-Name", "buyer")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.balance")
                .isEqualTo(5000);
    }

    @Test
    void shouldMakePayment() {
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(true);
        response.setBalance(BigDecimal.valueOf(9000));
        when(paymentService.makePayment(eq("buyer"), any(BigDecimal.class))).thenReturn(Mono.just(response));
        webTestClient
                .mutateWith(mockJwt())
                .mutateWith(csrf())
                .post()
                .uri("/payments")
                .header("X-User-Name", "buyer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        """
                        {
                          "amount": 1000
                        }
                        """)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.success")
                .isEqualTo(true)
                .jsonPath("$.balance")
                .isEqualTo(9000);
    }
}
