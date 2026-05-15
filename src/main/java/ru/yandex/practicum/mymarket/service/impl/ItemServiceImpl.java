package ru.yandex.practicum.mymarket.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.dto.ItemsPageDto;
import ru.yandex.practicum.mymarket.dto.PagingDto;
import ru.yandex.practicum.mymarket.entity.CartItem;
import ru.yandex.practicum.mymarket.entity.Item;
import ru.yandex.practicum.mymarket.enums.SortType;
import ru.yandex.practicum.mymarket.exception.ItemNotFoundException;
import ru.yandex.practicum.mymarket.mapper.ItemMapper;
import ru.yandex.practicum.mymarket.repository.CartItemRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.service.ItemService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private static final int ITEMS_PER_ROW = 3;
    private static final String EMPTY_SEARCH = "";
    private static final Long PLACEHOLDER_ID = -1L;
    private static final BigDecimal PLACEHOLDER_PRICE = BigDecimal.ZERO;
    private static final Integer PLACEHOLDER_COUNT = 0;
    private static final String EMPTY = "";

    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemMapper itemMapper;

    @Override
    public Mono<ItemsPageDto> getItemsPage(
            String search, SortType sortType, int pageNumber, int pageSize) {
        Pageable pageable = buildPageable(pageNumber, pageSize, sortType);
        String normalizedSearch = normalizeSearch(search);
        Mono<List<ItemDto>> itemsMono =
                itemRepository
                        .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                                normalizedSearch, normalizedSearch, pageable)
                        .collectList()
                        .flatMap(this::enrich);
        Mono<PagingDto> pagingMono = getPaging(search, sortType, pageNumber, pageSize);
        return Mono.zip(itemsMono, pagingMono)
                .map(tuple -> new ItemsPageDto(splitByRows(tuple.getT1()), tuple.getT2()));
    }

    @Override
    public Mono<PagingDto> getPaging(
            String search, SortType sortType, int pageNumber, int pageSize) {
        String normalizedSearch = normalizeSearch(search);
        return itemRepository
                .countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        normalizedSearch, normalizedSearch)
                .map(
                        totalItems -> {
                            boolean hasPrevious = pageNumber > 1;
                            boolean hasNext = (long) pageNumber * pageSize < totalItems;
                            return new PagingDto(pageSize, pageNumber, hasPrevious, hasNext);
                        });
    }

    @Override
    public Mono<ItemDto> getItem(Long id) {
        return itemRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new ItemNotFoundException()))
                .flatMap(this::enrich);
    }

    private Pageable buildPageable(int pageNumber, int pageSize, SortType sortType) {
        return PageRequest.of(Math.max(pageNumber - 1, 0), pageSize, sortType.getSort());
    }

    private String normalizeSearch(String search) {
        if (search == null || search.isBlank()) {
            return EMPTY_SEARCH;
        }
        return search.trim();
    }

    private List<List<ItemDto>> splitByRows(List<ItemDto> items) {
        List<List<ItemDto>> rows = new ArrayList<>();
        for (int i = 0; i < items.size(); i += ITEMS_PER_ROW) {
            List<ItemDto> row =
                    new ArrayList<>(items.subList(i, Math.min(i + ITEMS_PER_ROW, items.size())));
            while (row.size() < ITEMS_PER_ROW) {
                row.add(placeholder());
            }
            rows.add(row);
        }
        return rows;
    }

    private ItemDto placeholder() {
        return new ItemDto(
                PLACEHOLDER_ID, EMPTY, EMPTY, EMPTY, PLACEHOLDER_PRICE, PLACEHOLDER_COUNT);
    }

    private Mono<ItemDto> enrich(Item item) {
        return cartItemRepository
                .findByItemId(item.getId())
                .map(CartItem::getCount)
                .defaultIfEmpty(0)
                .map(count -> itemMapper.toDto(item, count));
    }

    private Mono<List<ItemDto>> enrich(List<Item> items) {
        if (items.isEmpty()) {
            return Mono.just(Collections.emptyList());
        }
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        return cartItemRepository
                .findAllByItemIdIn(itemIds)
                .collectMap(CartItem::getItemId, CartItem::getCount)
                .map(
                        counts ->
                                items.stream()
                                        .map(
                                                item ->
                                                        itemMapper.toDto(
                                                                item,
                                                                counts.getOrDefault(
                                                                        item.getId(), 0)))
                                        .toList());
    }
}
