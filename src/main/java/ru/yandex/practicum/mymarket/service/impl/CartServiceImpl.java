package ru.yandex.practicum.mymarket.service.impl;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.CartItemDto;
import ru.yandex.practicum.mymarket.entity.CartItem;
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
    public Mono<List<CartItemDto>> getCartItems() {
        return cartItemRepository.findAllByOrderByIdAsc().flatMap(this::toDto).collectList();
    }

    @Override
    public Mono<BigDecimal> getTotal() {
        return cartItemRepository
                .findAllByOrderByIdAsc()
                .flatMap(this::calculateItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public Mono<Void> changeCount(Long itemId, Action action) {
        return cartItemRepository
                .findByItemId(itemId)
                .flatMap(
                        cartItem ->
                                switch (action) {
                                    case PLUS -> increase(cartItem);
                                    case MINUS -> decrease(cartItem);
                                    case DELETE -> delete(cartItem);
                                })
                .switchIfEmpty(action == Action.PLUS ? createCartItem(itemId) : Mono.empty())
                .then();
    }

    private Mono<CartItemDto> toDto(CartItem cartItem) {
        return itemRepository
                .findById(cartItem.getItemId())
                .switchIfEmpty(Mono.error(new ItemNotFoundException()))
                .map(item -> cartItemMapper.toDto(cartItem, item));
    }

    private Mono<BigDecimal> calculateItemTotal(CartItem cartItem) {
        return itemRepository
                .findById(cartItem.getItemId())
                .map(item -> cartItem.getTotalPrice(item.getPrice()));
    }

    private Mono<CartItem> createCartItem(Long itemId) {
        return itemRepository
                .findById(itemId)
                .switchIfEmpty(Mono.error(new ItemNotFoundException()))
                .flatMap(
                        item -> {
                            CartItem cartItem =
                                    CartItem.builder().itemId(itemId).count(INITIAL_COUNT).build();
                            return cartItemRepository.save(cartItem);
                        });
    }

    private Mono<CartItem> increase(CartItem cartItem) {
        cartItem.setCount(cartItem.getCount() + 1);
        return cartItemRepository.save(cartItem);
    }

    private Mono<Void> decrease(CartItem cartItem) {
        int newCount = cartItem.getCount() - 1;
        if (newCount < MIN_COUNT) {
            return cartItemRepository.delete(cartItem);
        }
        cartItem.setCount(newCount);
        return cartItemRepository.save(cartItem).then();
    }

    private Mono<Void> delete(CartItem cartItem) {
        return cartItemRepository.delete(cartItem);
    }
}
