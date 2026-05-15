package ru.yandex.practicum.paymentservice.dto;

import java.math.BigDecimal;

public record OrderItemDto(String title, BigDecimal price, Integer count) {}
