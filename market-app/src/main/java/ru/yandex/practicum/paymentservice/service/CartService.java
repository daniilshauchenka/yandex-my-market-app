package ru.yandex.practicum.paymentservice.service;

import java.math.BigDecimal;
import java.util.List;

import ru.yandex.practicum.paymentservice.dto.CartItemDto;
import ru.yandex.practicum.paymentservice.enums.Action;

public interface CartService {

    List<CartItemDto> getCartItems();

    void changeCount(Long itemId, Action action);

    BigDecimal getTotal();
}
