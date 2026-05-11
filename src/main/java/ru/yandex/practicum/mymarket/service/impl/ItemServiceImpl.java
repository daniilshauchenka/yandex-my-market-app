package ru.yandex.practicum.mymarket.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.dto.ItemDto;
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

    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<List<ItemDto>> getItems(
        String search,
        SortType sortType,
        int pageNumber,
        int pageSize
    ) {

        Page<Item> page = getPage(
            search,
            sortType,
            pageNumber,
            pageSize
        );

        List<ItemDto> items = enrich(
            page.getContent()
        );

        return splitByRows(items);
    }

    @Override
    public PagingDto getPaging(
        String search,
        SortType sortType,
        int pageNumber,
        int pageSize
    ) {

        Page<Item> page = getPage(
            search,
            sortType,
            pageNumber,
            pageSize
        );

        return new PagingDto(
            pageSize,
            pageNumber,
            page.hasPrevious(),
            page.hasNext()
        );
    }

    @Override
    public ItemDto getItem(Long id) {

        Item item = itemRepository.findById(id)
            .orElseThrow(ItemNotFoundException::new);

        return enrich(item);
    }

    private Page<Item> getPage(
        String search,
        SortType sortType,
        int pageNumber,
        int pageSize
    ) {

        Pageable pageable = buildPageable(
            pageNumber,
            pageSize,
            sortType
        );

        String normalizedSearch = normalizeSearch(search);

        return itemRepository
            .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                normalizedSearch,
                normalizedSearch,
                pageable
            );
    }

    private Pageable buildPageable(
        int pageNumber,
        int pageSize,
        SortType sortType
    ) {

        return PageRequest.of(
            Math.max(pageNumber - 1, 0),
            pageSize,
            sortType.getSort()
        );
    }

    private String normalizeSearch(String search) {

        if (search == null || search.isBlank()) {
            return EMPTY_SEARCH;
        }

        return search.trim();
    }

    private List<List<ItemDto>> splitByRows(
        List<ItemDto> items
    ) {

        List<List<ItemDto>> rows = new ArrayList<>();

        for (int i = 0; i < items.size(); i += ITEMS_PER_ROW) {

            List<ItemDto> row = new ArrayList<>(
                items.subList(
                    i,
                    Math.min(
                        i + ITEMS_PER_ROW,
                        items.size()
                    )
                )
            );

            while (row.size() < ITEMS_PER_ROW) {
                row.add(placeholder());
            }

            rows.add(row);
        }

        return rows;
    }

    private ItemDto placeholder() {

        return new ItemDto(
            -1L,
            "",
            "",
            "",
            BigDecimal.ZERO,
            0
        );
    }

    private ItemDto enrich(Item item) {

        Integer count = cartItemRepository.findByItemId(item.getId())
            .map(CartItem::getCount)
            .orElse(0);

        ItemDto dto = itemMapper.toDto(item);

        return new ItemDto(
            dto.id(),
            dto.title(),
            dto.description(),
            dto.imgPath(),
            dto.price(),
            count
        );
    }

    private List<ItemDto> enrich(List<Item> items) {
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> itemIds = items.stream()
            .map(Item::getId)
            .toList();

        Map<Long, Integer> counts = cartItemRepository
            .findAllByItemIdIn(itemIds)
            .stream()
            .collect(Collectors.toMap(
                cartItem -> cartItem.getItem().getId(),
                CartItem::getCount
            ));

        return items.stream()
            .map(item -> {

                ItemDto dto = itemMapper.toDto(item);

                return new ItemDto(
                    dto.id(),
                    dto.title(),
                    dto.description(),
                    dto.imgPath(),
                    dto.price(),
                    counts.getOrDefault(item.getId(), 0)
                );
            })
            .toList();
    }
}