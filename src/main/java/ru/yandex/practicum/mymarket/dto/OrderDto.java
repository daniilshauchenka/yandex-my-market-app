package ru.yandex.practicum.mymarket.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(Long id, List<OrderItemDto> items, BigDecimal totalSum) {}
