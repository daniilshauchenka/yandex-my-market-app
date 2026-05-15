package ru.yandex.practicum.paymentservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.paymentservice.dto.CartItemDto;
import ru.yandex.practicum.paymentservice.entity.CartItem;
import ru.yandex.practicum.paymentservice.entity.Item;
import ru.yandex.practicum.paymentservice.enums.Action;
import ru.yandex.practicum.paymentservice.mapper.CartItemMapper;
import ru.yandex.practicum.paymentservice.repository.CartItemRepository;
import ru.yandex.practicum.paymentservice.repository.ItemRepository;
import ru.yandex.practicum.paymentservice.service.impl.CartServiceImpl;
import ru.yandex.practicum.paymentservice.util.TestData;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

  @Mock private CartItemRepository cartItemRepository;

  @Mock private ItemRepository itemRepository;

  @Mock private CartItemMapper cartItemMapper;

  @InjectMocks private CartServiceImpl cartService;

  @Test
  void shouldReturnCartItems() {

    List<CartItem> cartItems = List.of(TestData.cartItem());

    List<CartItemDto> expected = List.of(TestData.cartItemDto());

    when(cartItemRepository.findAllByOrderByIdAsc()).thenReturn(cartItems);

    when(cartItemMapper.toDtoList(cartItems)).thenReturn(expected);

    List<CartItemDto> actual = cartService.getCartItems();

    assertThat(actual).isEqualTo(expected);

    verify(cartItemRepository).findAllByOrderByIdAsc();

    verify(cartItemMapper).toDtoList(cartItems);
  }

  @Test
  void shouldCalculateTotal() {

    CartItem first = TestData.cartItem(BigDecimal.valueOf(100), 2);

    CartItem second = TestData.cartItem(BigDecimal.valueOf(50), 3);

    when(cartItemRepository.findAll()).thenReturn(List.of(first, second));

    BigDecimal total = cartService.getTotal();

    assertThat(total).isEqualByComparingTo("350");
  }

  @Test
  void shouldCreateCartItemWhenPlusAction() {

    Long itemId = 1L;

    Item item = TestData.item();

    when(cartItemRepository.findByItemId(itemId)).thenReturn(Optional.empty());

    when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

    cartService.changeCount(itemId, Action.PLUS);

    verify(cartItemRepository).save(any(CartItem.class));
  }

  @Test
  void shouldIncreaseCount() {

    CartItem cartItem = TestData.cartItem();

    cartItem.setCount(1);

    when(cartItemRepository.findByItemId(1L)).thenReturn(Optional.of(cartItem));

    cartService.changeCount(1L, Action.PLUS);

    assertThat(cartItem.getCount()).isEqualTo(2);

    verify(cartItemRepository).save(cartItem);
  }

  @Test
  void shouldDecreaseCount() {

    CartItem cartItem = TestData.cartItem();

    cartItem.setCount(2);

    when(cartItemRepository.findByItemId(1L)).thenReturn(Optional.of(cartItem));

    cartService.changeCount(1L, Action.MINUS);

    assertThat(cartItem.getCount()).isEqualTo(1);

    verify(cartItemRepository).save(cartItem);
  }

  @Test
  void shouldDeleteCartItemWhenCountBecomesZero() {

    CartItem cartItem = TestData.cartItem();

    cartItem.setCount(1);

    when(cartItemRepository.findByItemId(1L)).thenReturn(Optional.of(cartItem));

    cartService.changeCount(1L, Action.MINUS);

    verify(cartItemRepository).delete(cartItem);
  }

  @Test
  void shouldDeleteCartItem() {

    CartItem cartItem = TestData.cartItem();

    when(cartItemRepository.findByItemId(1L)).thenReturn(Optional.of(cartItem));

    cartService.changeCount(1L, Action.DELETE);

    verify(cartItemRepository).delete(cartItem);
  }
}
