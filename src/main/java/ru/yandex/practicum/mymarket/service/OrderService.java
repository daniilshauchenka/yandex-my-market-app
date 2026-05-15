package ru.yandex.practicum.mymarket.service;

import java.util.List;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.OrderDto;

public interface OrderService {

    Mono<List<OrderDto>> getOrders();

    Mono<OrderDto> getOrder(Long id);

    Mono<Long> buy();
}
