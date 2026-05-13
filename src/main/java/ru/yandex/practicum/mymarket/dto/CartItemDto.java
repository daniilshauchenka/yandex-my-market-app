package ru.yandex.practicum.mymarket.dto;

import java.math.BigDecimal;

public record CartItemDto(
        Long id,
        String title,
        String description,
        String imgPath,
        BigDecimal price,
        Integer count) {}
