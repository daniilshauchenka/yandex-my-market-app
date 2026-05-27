package ru.yandex.practicum.paymentservice.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.paymentservice.config.security.CurrentUserService;
import ru.yandex.practicum.paymentservice.dto.ItemDto;
import ru.yandex.practicum.paymentservice.dto.PagingDto;
import ru.yandex.practicum.paymentservice.entity.CartItem;
import ru.yandex.practicum.paymentservice.entity.Item;
import ru.yandex.practicum.paymentservice.enums.SortType;
import ru.yandex.practicum.paymentservice.exception.ItemNotFoundException;
import ru.yandex.practicum.paymentservice.mapper.ItemMapper;
import ru.yandex.practicum.paymentservice.repository.CartItemRepository;
import ru.yandex.practicum.paymentservice.repository.ItemRepository;
import ru.yandex.practicum.paymentservice.service.ItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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

    private final CurrentUserService currentUserService;

    @Override
    @Cacheable(value = "items-page", key = "#search + '-' + #sortType + '-' + #pageNumber + '-' + #pageSize")
    public List<List<ItemDto>> getItems(String search, SortType sortType, int pageNumber, int pageSize) {
        log.info("Loading items page from database");
        Page<Item> page = getPage(search, sortType, pageNumber, pageSize);
        List<ItemDto> items = enrich(page.getContent());
        return splitByRows(items);
    }

    @Override
    public PagingDto getPaging(String search, SortType sortType, int pageNumber, int pageSize) {
        Page<Item> page = getPage(search, sortType, pageNumber, pageSize);
        return new PagingDto(pageSize, pageNumber, page.hasPrevious(), page.hasNext());
    }

    @Override
    @Cacheable(value = "items", key = "#id")
    public ItemDto getItem(Long id) {
        log.info("Loading item {} from database", id);
        Item item = itemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        return enrich(item);
    }

    private Page<Item> getPage(String search, SortType sortType, int pageNumber, int pageSize) {
        Pageable pageable = buildPageable(pageNumber, pageSize, sortType);
        String normalizedSearch = normalizeSearch(search);
        return itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                normalizedSearch, normalizedSearch, pageable);
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

            List<ItemDto> row = new ArrayList<>(items.subList(i, Math.min(i + ITEMS_PER_ROW, items.size())));

            while (row.size() < ITEMS_PER_ROW) {
                row.add(placeholder());
            }

            rows.add(row);
        }
        return rows;
    }

    private ItemDto placeholder() {
        return new ItemDto(PLACEHOLDER_ID, EMPTY, EMPTY, EMPTY, PLACEHOLDER_PRICE, PLACEHOLDER_COUNT);
    }

    private ItemDto enrich(Item item) {
        Integer count = currentUserService
                .getCurrentUser()
                .flatMap(user -> cartItemRepository.findByUserIdAndItemId(user.getId(), item.getId()))
                .map(CartItem::getCount)
                .orElse(0);
        return itemMapper.toDto(item, count);
    }

    private List<ItemDto> enrich(List<Item> items) {
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        Long userId = currentUserService.getCurrentUser().map(u -> u.getId()).orElse(null);
        if (userId == null) {
            return items.stream().map(item -> itemMapper.toDto(item, 0)).toList();
        }
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        Map<Long, Integer> counts = cartItemRepository.findAllByUserIdAndItemIdIn(userId, itemIds).stream()
                .collect(Collectors.toMap(cartItem -> cartItem.getItem().getId(), CartItem::getCount));
        return items.stream()
                .map(item -> itemMapper.toDto(item, counts.getOrDefault(item.getId(), 0)))
                .toList();
    }
}
