package ru.yandex.practicum.paymentservice.dto;

import java.math.BigDecimal;

public record ItemDto(
    Long id, String title, String description, String imgPath, BigDecimal price, Integer count) {}
