package ru.yandex.practicum.paymentservice.config;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment")
public record PaymentProperties(BigDecimal initialBalance) {}
