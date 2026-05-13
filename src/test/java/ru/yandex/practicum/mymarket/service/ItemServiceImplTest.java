package ru.yandex.practicum.mymarket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.dto.ItemDto;
import ru.yandex.practicum.mymarket.dto.PagingDto;
import ru.yandex.practicum.mymarket.entity.CartItem;
import ru.yandex.practicum.mymarket.entity.Item;
import ru.yandex.practicum.mymarket.enums.SortType;
import ru.yandex.practicum.mymarket.exception.ItemNotFoundException;
import ru.yandex.practicum.mymarket.mapper.ItemMapper;
import ru.yandex.practicum.mymarket.repository.CartItemRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.service.impl.ItemServiceImpl;
import ru.yandex.practicum.mymarket.util.TestData;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void shouldReturnItem() {
        Item item = TestData.item();
        CartItem cartItem = TestData.cartItem();
        ItemDto dto = new ItemDto(
            item.getId(),
            item.getTitle(),
            item.getDescription(),
            item.getImgPath(),
            item.getPrice(),
            cartItem.getCount()
        );
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item));
        when(cartItemRepository.findByItemId(1L)).thenReturn(Mono.just(cartItem));
        when(itemMapper.toDto(item, cartItem.getCount())).thenReturn(dto);
        StepVerifier.create(itemService.getItem(1L))
            .assertNext(actual ->
                assertThat(actual.count()).isEqualTo(cartItem.getCount()))
            .verifyComplete();
    }

    @Test
    void shouldThrowWhenItemNotFound() {
        when(itemRepository.findById(1L))
            .thenReturn(Mono.empty());
        StepVerifier.create(itemService.getItem(1L))
            .expectError(ItemNotFoundException.class)
            .verify();
    }

    @Test
    void shouldReturnItems() {
        Item item = TestData.item();
        ItemDto dto = TestData.itemDto();
        when(itemRepository
            .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                anyString(),
                anyString(),
                any(PageRequest.class)))
            .thenReturn(Flux.just(item));
        when(cartItemRepository.findAllByItemIdIn(any())).thenReturn(Flux.empty());
        when(itemMapper.toDto(item, 0)).thenReturn(dto);
        when(itemRepository
            .countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                anyString(),
                anyString()))
            .thenReturn(Mono.just(1L));
        StepVerifier.create(
                itemService.getItemsPage("", SortType.NO, 1, 5))
            .assertNext(
                page -> {
                    assertThat(page.items()).hasSize(1);
                    assertThat(page.items().getFirst())
                        .hasSize(3);
                    assertThat(page.items()
                        .getFirst()
                        .getFirst())
                        .isEqualTo(dto);
                })
            .verifyComplete();
    }

    @Test
    void shouldReturnPaging() {
        when(itemRepository.countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            anyString(),
            anyString()
        )).thenReturn(Mono.just(1L));
        StepVerifier.create(
                itemService.getPaging("", SortType.NO, 1, 5)
            )
            .assertNext(paging -> {
                assertThat(paging.pageNumber()).isEqualTo(1);
                assertThat(paging.pageSize()).isEqualTo(5);
            })
            .verifyComplete();
    }
}