package ru.yandex.practicum.paymentservice.service;

import java.util.List;
import ru.yandex.practicum.paymentservice.dto.OrderDto;

public interface OrderService {

  List<OrderDto> getOrders();

  OrderDto getOrder(Long id);

  Long buy();
}
