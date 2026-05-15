package ru.yandex.practicum.paymentservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.paymentservice.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
  Page<Item> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
      String title, String description, Pageable pageable);
}
