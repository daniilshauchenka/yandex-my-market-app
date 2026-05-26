package ru.yandex.practicum.paymentservice.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.paymentservice.config.security.CurrentUserService;
import ru.yandex.practicum.paymentservice.dto.CartItemDto;
import ru.yandex.practicum.paymentservice.entity.CartItem;
import ru.yandex.practicum.paymentservice.entity.Item;
import ru.yandex.practicum.paymentservice.enums.Action;
import ru.yandex.practicum.paymentservice.exception.ItemNotFoundException;
import ru.yandex.practicum.paymentservice.mapper.CartItemMapper;
import ru.yandex.practicum.paymentservice.repository.CartItemRepository;
import ru.yandex.practicum.paymentservice.repository.ItemRepository;
import ru.yandex.practicum.paymentservice.service.CartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private static final int INITIAL_COUNT = 1;
    private static final int MIN_COUNT = 1;

    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final CartItemMapper cartItemMapper;
    private final CurrentUserService currentUserService;

    @Override
    public List<CartItemDto> getCartItems() {
        Long userId = currentUserService.requireCurrentUser().getId();
        List<CartItem> cartItems = cartItemRepository.findAllByUserIdOrderByIdAsc(userId);
        return cartItemMapper.toDtoList(cartItems);
    }

    @Override
    public BigDecimal getTotal() {
        Long userId = currentUserService.requireCurrentUser().getId();
        return cartItemRepository.findAllByUserIdOrderByIdAsc(userId).stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public void changeCount(Long itemId, Action action) {
        Long userId = currentUserService.requireCurrentUser().getId();
        CartItem cartItem = cartItemRepository.findByUserIdAndItemId(userId, itemId).orElse(null);

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
        CartItem cartItem = CartItem.builder()
                .user(currentUserService.requireCurrentUser())
                .item(item)
                .count(INITIAL_COUNT)
                .build();
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
