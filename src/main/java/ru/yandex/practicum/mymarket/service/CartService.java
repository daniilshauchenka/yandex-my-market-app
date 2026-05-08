package ru.yandex.practicum.mymarket.service;

import java.util.List;
import ru.yandex.practicum.mymarket.dto.CartItemDto;

public interface CartService {

    List<CartItemDto> getCartItems();
}
