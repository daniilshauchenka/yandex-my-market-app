package ru.yandex.practicum.mymarket.dto;

import java.math.BigDecimal;

public record OrderItemDto(
    String title,
    BigDecimal price,
    Integer count
) {
}