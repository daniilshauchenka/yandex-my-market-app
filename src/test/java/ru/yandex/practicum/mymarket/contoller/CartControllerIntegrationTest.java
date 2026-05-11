package ru.yandex.practicum.mymarket.contoller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.util.AbstractIntegrationTest;

@Transactional
class CartControllerIntegrationTest extends AbstractIntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnCartPage() throws Exception {

    mockMvc
        .perform(get("/cart/items"))
        .andExpect(status().isOk())
        .andExpect(view().name("cart"))
        .andExpect(model().attributeExists("items"))
        .andExpect(model().attributeExists("total"));
  }

  @Test
  void shouldChangeCartItem() throws Exception {

    mockMvc
        .perform(post("/cart/items").param("id", "1").param("action", "PLUS"))
        .andExpect(status().isOk())
        .andExpect(view().name("cart"));
  }
}
