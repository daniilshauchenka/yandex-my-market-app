package ru.yandex.practicum.mymarket.service;

import java.util.List;
import ru.yandex.practicum.mymarket.dto.OrderDto;

public interface OrderService {

    List<OrderDto> getOrders();

    OrderDto getOrder(Long id);

    Long buy();
}