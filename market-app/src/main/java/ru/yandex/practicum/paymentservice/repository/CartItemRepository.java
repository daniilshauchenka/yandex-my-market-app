package ru.yandex.practicum.paymentservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.yandex.practicum.paymentservice.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByUserIdAndItemId(Long userId, Long itemId);

    List<CartItem> findAllByUserIdOrderByIdAsc(Long userId);

    void deleteByUserIdAndItemId(Long userId, Long itemId);

    List<CartItem> findAllByUserIdAndItemIdIn(Long userId, List<Long> itemIds);
}
