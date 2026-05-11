package ru.yandex.practicum.mymarket.service;

import java.math.BigDecimal;
import java.util.List;
import ru.yandex.practicum.mymarket.dto.CartItemDto;
import ru.yandex.practicum.mymarket.enums.Action;

public interface CartService {

    List<CartItemDto> getCartItems();

    void changeCount(Long itemId, Action action);

    BigDecimal getTotal();

}
