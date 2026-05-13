package ru.yandex.practicum.mymarket.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static
    org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import ru.yandex.practicum.mymarket.util.AbstractIntegrationTest;


class ItemControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturnItemsPage() {
        webTestClient.get()
            .uri("/items")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .consumeWith(response ->
                assertThat(response.getResponseBody())
                    .contains("items"));
    }
    @Test
    void shouldReturnItemPage() {
        webTestClient.get()
            .uri("/items/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .consumeWith(response ->
                assertThat(response.getResponseBody())
                    .contains("Ball"));
    }

    @Test
    void shouldReturn404WhenItemNotFound() {
        webTestClient.get()
            .uri("/items/999999")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void shouldAddItemToCart() {
        webTestClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/items")
                .queryParam("id", 1)
                .queryParam("action", "PLUS")
                .queryParam("search", "")
                .queryParam("sort", "NO")
                .queryParam("pageNumber", 1)
                .queryParam("pageSize", 5)
                .build());
    }

    @Test
    void shouldChangeItemCountFromItemPage() {
        webTestClient.post()
            .uri("/items/1")
            .bodyValue("action=PLUS")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueEquals("Location", "/items/1");
    }
}
