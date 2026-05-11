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

public final class TestData {

    private TestData() {
    }

    public static Item item() {
        return Item.builder()
            .id(1L)
            .title("Ball")
            .description("Desc")
            .imgPath("/img")
            .price(BigDecimal.valueOf(100))
            .build();
    }

    public static ItemDto itemDto() {
        return new ItemDto(
            1L,
            "Ball",
            "Desc",
            "/img",
            BigDecimal.valueOf(100),
            1
        );
    }

    public static OrderItemDto orderItemDto() {

        return new OrderItemDto(
            "Ball",
            BigDecimal.valueOf(100),
            1
        );
    }

    public static CartItem cartItem() {
        return cartItem(
            BigDecimal.valueOf(100),
            1
        );
    }

    public static CartItem cartItem(
        BigDecimal price,
        Integer count
    ) {

        return CartItem.builder()
            .id(1L)
            .item(
                Item.builder()
                    .id(1L)
                    .title("Ball")
                    .price(price)
                    .build()
            )
            .count(count)
            .build();
    }

    public static CartItemDto cartItemDto() {
        return new CartItemDto(
            1L,
            "Ball",
            "Description",
            null,
            BigDecimal.ONE,
            1
        );
    }

    public static Order order() {
        return Order.builder()
            .id(1L)
            .totalSum(BigDecimal.valueOf(100))
            .items(new ArrayList<>())
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