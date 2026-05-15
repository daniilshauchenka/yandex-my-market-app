package ru.yandex.practicum.paymentservice.dto;

public record PagingDto(int pageSize, int pageNumber, boolean hasPrevious, boolean hasNext) {}
