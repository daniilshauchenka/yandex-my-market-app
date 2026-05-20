package ru.yandex.practicum.paymentservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.yandex.practicum.paymentservice.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByOrderByIdDesc();
}
