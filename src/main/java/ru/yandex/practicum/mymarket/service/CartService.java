package ru.yandex.practicum.mymarket.service;

import java.math.BigDecimal;
import java.util.List;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.CartItemDto;
import ru.yandex.practicum.mymarket.enums.Action;

public interface CartService {

    Mono<List<CartItemDto>> getCartItems();

    Mono<BigDecimal> getTotal();

    Mono<Void> changeCount(Long itemId, Action action);
}
