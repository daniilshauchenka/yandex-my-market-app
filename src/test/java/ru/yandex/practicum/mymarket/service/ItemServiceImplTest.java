package ru.yandex.practicum.mymarket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

  @Mock private ItemRepository itemRepository;

  @Mock private CartItemRepository cartItemRepository;

  @Mock private ItemMapper itemMapper;

  @InjectMocks private ItemServiceImpl itemService;

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
        when(itemRepository.findById(1L))
            .thenReturn(Optional.of(item));
        when(cartItemRepository.findByItemId(1L))
            .thenReturn(Optional.of(cartItem));
        when(itemMapper.toDto(item, cartItem.getCount()))
            .thenReturn(dto);
        ItemDto actual = itemService.getItem(1L);
        assertThat(actual.count())
            .isEqualTo(cartItem.getCount());
    }

  @Test
  void shouldThrowWhenItemNotFound() {

    when(itemRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> itemService.getItem(1L)).isInstanceOf(ItemNotFoundException.class);
  }

  @Test
  void shouldReturnItemsWithPlaceholders() {

    Item item = TestData.item();

    ItemDto dto = TestData.itemDto();

    Page<Item> page = new PageImpl<>(List.of(item));

    when(itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            anyString(), anyString(), any(Pageable.class)))
        .thenReturn(page);

      when(itemMapper.toDto(item, 0))
          .thenReturn(dto);

    when(cartItemRepository.findAllByItemIdIn(any())).thenReturn(Collections.emptyList());

    List<List<ItemDto>> result = itemService.getItems("", SortType.NO, 1, 5);

    assertThat(result).hasSize(1);

    assertThat(result.getFirst()).hasSize(3);

    assertThat(result.getFirst().get(1).id()).isEqualTo(-1L);
  }

  @Test
  void shouldReturnPaging() {

    Page<Item> page = new PageImpl<>(List.of(TestData.item()));

    when(itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            anyString(), anyString(), any(Pageable.class)))
        .thenReturn(page);

    PagingDto paging = itemService.getPaging("", SortType.NO, 1, 5);

    assertThat(paging.pageNumber()).isEqualTo(1);

    assertThat(paging.pageSize()).isEqualTo(5);
  }
}
