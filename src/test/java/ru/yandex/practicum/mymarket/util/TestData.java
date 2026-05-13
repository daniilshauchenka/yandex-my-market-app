package ru.yandex.practicum.mymarket.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import ru.yandex.practicum.mymarket.dto.CartItemDto;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.dto.OrderDto;
import ru.yandex.practicum.mymarket.dto.OrderItemDto;
import ru.yandex.practicum.mymarket.entity.CartItem;
import ru.yandex.practicum.mymarket.entity.Item;
import ru.yandex.practicum.mymarket.entity.Order;
import ru.yandex.practicum.mymarket.entity.OrderItem;

public final class TestData {

    private TestData() {
    }

    public static ItemDto itemDto() {
        return new ItemDto(1L, "Ball", "Desc", "/img", BigDecimal.valueOf(100), 1);
    }

    public static OrderItem orderItem() {
        return OrderItem.builder()
            .title("Ball")
            .price(BigDecimal.valueOf(100))
            .count(1)
            .build();
    }

    public static OrderItemDto orderItemDto() {
        return new OrderItemDto("Ball", BigDecimal.valueOf(100), 1);
    }

    public static CartItem cartItem() {
        return cartItem(1L, 1L, 1);
    }

    public static CartItem cartItem(Long id, Long itemId, Integer count) {
        return CartItem.builder()
            .id(id)
            .itemId(itemId)
            .count(count)
            .build();
    }

    public static Item item() {
        return item(1L, BigDecimal.valueOf(100));
    }

    public static CartItemDto cartItemDto() {
        return new CartItemDto(1L, "Ball", "Description", null, BigDecimal.ONE, 1);
    }

    public static Item item(Long id, BigDecimal price) {
        return Item.builder()
            .id(id)
            .title("Ball")
            .description("Desc")
            .imgPath("/img")
            .price(price)
            .build();
    }

    public static Order order() {
        return Order.builder()
            .id(1L)
            .totalSum(BigDecimal.valueOf(100))
            .build();
    }

    public static OrderDto orderDto() {
        return new OrderDto(
            1L,
            List.of(orderItemDto()),
            BigDecimal.valueOf(100)
        );
    }
}
