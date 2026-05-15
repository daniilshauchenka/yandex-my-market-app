package ru.yandex.practicum.mymarket.repository;

import java.util.Collection;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.entity.CartItem;

public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {

    Mono<CartItem> findByItemId(Long itemId);

    Flux<CartItem> findAllByOrderByIdAsc();

    Flux<CartItem> findAllByItemIdIn(Collection<Long> itemIds);
}
