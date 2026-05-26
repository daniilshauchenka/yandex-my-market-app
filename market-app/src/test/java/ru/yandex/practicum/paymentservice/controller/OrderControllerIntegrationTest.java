package ru.yandex.practicum.paymentservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.paymentservice.entity.CartItem;
import ru.yandex.practicum.paymentservice.entity.Item;
import ru.yandex.practicum.paymentservice.entity.User;
import ru.yandex.practicum.paymentservice.repository.CartItemRepository;
import ru.yandex.practicum.paymentservice.repository.ItemRepository;
import ru.yandex.practicum.paymentservice.repository.UserRepository;
import ru.yandex.practicum.paymentservice.util.AbstractIntegrationTest;

@Transactional
class OrderControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        cartItemRepository.deleteAll();
        User user = userRepository.findByUsername("buyer").orElseThrow();
        Item item = itemRepository.findById(1L).orElseThrow();
        CartItem cartItem = CartItem.builder().user(user).item(item).count(2).build();
        cartItemRepository.save(cartItem);

        when(paymentGatewayService.pay(any())).thenReturn(true);
        when(paymentGatewayService.isAvailable()).thenReturn(true);
    }

    @Test
    @WithMockUser(username = "buyer", roles = "USER")
    void shouldCreateOrder() throws Exception {
        mockMvc.perform(post("/buy").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/orders/*?newOrder=true"));
    }

    @Test
    @WithMockUser(username = "buyer", roles = "USER")
    void shouldReturnOrdersPage() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));
    }
}
