package ru.yandex.practicum.mymarket.contoller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.util.AbstractIntegrationTest;

@Transactional
class ItemControllerIntegrationTest extends AbstractIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnItemsPage() throws Exception {

    mockMvc
        .perform(get("/items"))
        .andExpect(status().isOk())
        .andExpect(view().name("items"))
        .andExpect(model().attributeExists("items"))
        .andExpect(model().attributeExists("paging"));
  }

  @Test
  void shouldReturnItemPage() throws Exception {

    mockMvc
        .perform(get("/items/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("item"))
        .andExpect(model().attributeExists("item"));
  }

  @Test
  void shouldReturn404WhenItemNotFound() throws Exception {

    mockMvc.perform(get("/items/999999")).andExpect(status().isNotFound());
  }

  @Test
  void shouldAddItemToCart() throws Exception {

    mockMvc
        .perform(post("/items").param("id", "1").param("action", "PLUS"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("/items**"));
  }
}
