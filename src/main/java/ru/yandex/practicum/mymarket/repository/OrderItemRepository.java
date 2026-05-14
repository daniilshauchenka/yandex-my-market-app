package ru.yandex.practicum.mymarket.repository;

import java.util.List;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.mymarket.entity.OrderItem;

public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {

    Flux<OrderItem> findAllByOrderId(Long orderId);

    Flux<OrderItem> findAllByOrderIdIn(List<Long> orderIds);
}
