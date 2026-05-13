package ru.yandex.practicum.mymarket.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.entity.CartItem;
import ru.yandex.practicum.mymarket.entity.Item;
import ru.yandex.practicum.mymarket.repository.CartItemRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.util.AbstractIntegrationTest;

@Transactional
class OrderControllerIntegrationTest extends AbstractIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private CartItemRepository cartItemRepository;

  @Autowired private ItemRepository itemRepository;

  @BeforeEach
  void setup() {
    cartItemRepository.deleteAll();
    Item item = itemRepository.findById(1L).orElseThrow();
    CartItem cartItem = CartItem.builder().item(item).count(2).build();
    cartItemRepository.save(cartItem);
  }

  @Test
  void shouldCreateOrder() throws Exception {
    mockMvc
        .perform(post("/buy"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("/orders/*?newOrder=true"));
  }

  @Test
  void shouldReturnOrdersPage() throws Exception {
    mockMvc
        .perform(get("/orders"))
        .andExpect(status().isOk())
        .andExpect(view().name("orders"))
        .andExpect(model().attributeExists("orders"));
  }
}
