package ru.yandex.practicum.paymentservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.yandex.practicum.paymentservice.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByItemId(Long itemId);

    List<CartItem> findAllByOrderByIdAsc();

    void deleteByItemId(Long itemId);

    List<CartItem> findAllByItemIdIn(List<Long> itemIds);
}
