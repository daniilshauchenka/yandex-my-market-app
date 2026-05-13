package ru.yandex.practicum.mymarket.service.impl;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mymarket.dto.CartItemDto;
import ru.yandex.practicum.mymarket.entity.CartItem;
import ru.yandex.practicum.mymarket.entity.Item;
import ru.yandex.practicum.mymarket.enums.Action;
import ru.yandex.practicum.mymarket.exception.ItemNotFoundException;
import ru.yandex.practicum.mymarket.mapper.CartItemMapper;
import ru.yandex.practicum.mymarket.repository.CartItemRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.service.CartService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

  private static final int INITIAL_COUNT = 1;
  private static final int MIN_COUNT = 1;

  private final CartItemRepository cartItemRepository;
  private final ItemRepository itemRepository;
  private final CartItemMapper cartItemMapper;

  @Override
  public List<CartItemDto> getCartItems() {
    List<CartItem> cartItems = cartItemRepository.findAllByOrderByIdAsc();
    return cartItemMapper.toDtoList(cartItems);
  }

  @Override
  public BigDecimal getTotal() {
    return cartItemRepository.findAll().stream()
        .map(CartItem::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  @Transactional
  public void changeCount(Long itemId, Action action) {
    CartItem cartItem = cartItemRepository.findByItemId(itemId).orElse(null);

    if (cartItem == null) {
      if (action != Action.PLUS) {
        return;
      }
      createCartItem(itemId);
      return;
    }

    switch (action) {
      case PLUS -> increase(cartItem);
      case MINUS -> decrease(cartItem);
      case DELETE -> delete(cartItem);
    }
  }

  private void createCartItem(Long itemId) {
    Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
    CartItem cartItem = CartItem.builder().item(item).count(INITIAL_COUNT).build();
    cartItemRepository.save(cartItem);
  }

  private void increase(CartItem cartItem) {
    cartItem.setCount(cartItem.getCount() + 1);
    cartItemRepository.save(cartItem);
  }

  private void decrease(CartItem cartItem) {
    int newCount = cartItem.getCount() - 1;
    if (newCount < MIN_COUNT) {
      cartItemRepository.delete(cartItem);
      return;
    }
    cartItem.setCount(newCount);
    cartItemRepository.save(cartItem);
  }

  private void delete(CartItem cartItem) {
    cartItemRepository.delete(cartItem);
  }
}
