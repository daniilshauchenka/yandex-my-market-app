package ru.yandex.practicum.mymarket.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.mymarket.entity.CartItem;
import ru.yandex.practicum.mymarket.entity.Item;
import ru.yandex.practicum.mymarket.repository.CartItemRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.util.AbstractIntegrationTest;

class OrderControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setup() {
        cartItemRepository.deleteAll().block();
        Item item = itemRepository.findById(1L).block();
        CartItem cartItem = CartItem.builder()
            .itemId(item.getId())
            .count(2)
            .build();
        cartItemRepository.save(cartItem).block();
    }

    @Test
    void shouldCreateOrder() {
        webTestClient.post()
            .uri("/buy")
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader()
            .valueMatches("Location", "/orders/.+\\?newOrder=true");
    }

    @Test
    void shouldReturnOrdersPage() {
        webTestClient.get()
            .uri("/orders")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .consumeWith(response ->
                assertThat(response.getResponseBody())
                    .contains("Витрина магазина"));
    }
}