package ru.yandex.practicum.paymentservice.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(Long id, List<OrderItemDto> items, BigDecimal totalSum) {}
