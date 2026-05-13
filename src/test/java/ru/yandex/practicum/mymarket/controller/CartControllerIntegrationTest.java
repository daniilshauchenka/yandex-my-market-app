package ru.yandex.practicum.mymarket.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.util.AbstractIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.mymarket.util.AbstractIntegrationTest;

class CartControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturnCartPage() {
        webTestClient.get()
            .uri("/cart/items")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .consumeWith(response ->
                assertThat(response.getResponseBody())
                    .contains("cart"));
    }

    @Test
    void shouldChangeCartItem() {
        webTestClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/cart/items")
                .queryParam("id", 1)
                .queryParam("action", "PLUS")
                .build())
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader()
            .valueEquals("Location", "/cart/items");
    }
}