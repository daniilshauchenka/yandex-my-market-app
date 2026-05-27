package ru.yandex.practicum.paymentservice.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartSummary(List<CartItemDto> items, BigDecimal total) {}

