package ru.yandex.practicum.mymarket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import org.mockito.stubbing.OngoingStubbing;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.CartItemDto;
import ru.yandex.practicum.mymarket.entity.CartItem;
import ru.yandex.practicum.mymarket.entity.Item;
import ru.yandex.practicum.mymarket.enums.Action;
import ru.yandex.practicum.mymarket.mapper.CartItemMapper;
import ru.yandex.practicum.mymarket.repository.CartItemRepository;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.service.impl.CartServiceImpl;
import ru.yandex.practicum.mymarket.util.TestData;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CartItemMapper cartItemMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void shouldReturnCartItems() {
        CartItem cartItem = TestData.cartItem();
        Item item = TestData.item();
        CartItemDto dto = TestData.cartItemDto();

        when(cartItemRepository.findAllByOrderByIdAsc())
            .thenReturn(Flux.just(cartItem));

        when(itemRepository.findById(cartItem.getItemId()))
            .thenReturn(Mono.just(item));

        when(cartItemMapper.toDto(cartItem, item))
            .thenReturn(dto);

        StepVerifier.create(cartService.getCartItems())
            .expectNext(List.of(dto))
            .verifyComplete();

        verify(cartItemRepository).findAllByOrderByIdAsc();
        verify(itemRepository).findById(cartItem.getItemId());
        verify(cartItemMapper).toDto(cartItem, item);
    }

    @Test
    void shouldCalculateTotal() {
        CartItem first = TestData.cartItem(1L, 1L, 2);
        CartItem second = TestData.cartItem(2L, 2L, 3);

        Item firstItem = TestData.item(1L, BigDecimal.valueOf(100));
        Item secondItem = TestData.item(2L, BigDecimal.valueOf(50));

        when(cartItemRepository.findAllByOrderByIdAsc())
            .thenReturn(Flux.just(first, second));

        when(itemRepository.findById(1L))
            .thenReturn(Mono.just(firstItem));

        when(itemRepository.findById(2L))
            .thenReturn(Mono.just(secondItem));

        StepVerifier.create(cartService.getTotal())
            .assertNext(total -> assertThat(total).isEqualByComparingTo("350"))
            .verifyComplete();
    }

    @Test
    void shouldCreateCartItemWhenPlusAction() {
        Long itemId = 1L;
        Item item = TestData.item();

        when(cartItemRepository.findByItemId(itemId))
            .thenReturn(Mono.empty());

        when(itemRepository.findById(itemId))
            .thenReturn(Mono.just(item));

        when(cartItemRepository.save(any(CartItem.class)))
            .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(cartService.changeCount(itemId, Action.PLUS))
            .verifyComplete();

        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void shouldIncreaseCount() {
        CartItem cartItem = TestData.cartItem();
        cartItem.setCount(1);

        when(cartItemRepository.findByItemId(1L))
            .thenReturn(Mono.just(cartItem));

        when(cartItemRepository.save(cartItem))
            .thenReturn(Mono.just(cartItem));

        StepVerifier.create(cartService.changeCount(1L, Action.PLUS))
            .verifyComplete();

        assertThat(cartItem.getCount()).isEqualTo(2);
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void shouldDecreaseCount() {
        CartItem cartItem = TestData.cartItem();
        cartItem.setCount(2);

        when(cartItemRepository.findByItemId(1L))
            .thenReturn(Mono.just(cartItem));

        when(cartItemRepository.save(cartItem))
            .thenReturn(Mono.just(cartItem));

        StepVerifier.create(cartService.changeCount(1L, Action.MINUS))
            .verifyComplete();

        assertThat(cartItem.getCount()).isEqualTo(1);
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void shouldDeleteCartItemWhenCountBecomesZero() {
        CartItem cartItem = TestData.cartItem();
        cartItem.setCount(1);

        when(cartItemRepository.findByItemId(1L))
            .thenReturn(Mono.just(cartItem));

        when(cartItemRepository.delete(cartItem))
            .thenReturn(Mono.empty());

        StepVerifier.create(cartService.changeCount(1L, Action.MINUS))
            .verifyComplete();

        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void shouldDeleteCartItem() {
        CartItem cartItem = TestData.cartItem();

        when(cartItemRepository.findByItemId(1L))
            .thenReturn(Mono.just(cartItem));

        when(cartItemRepository.delete(cartItem))
            .thenReturn(Mono.empty());

        StepVerifier.create(cartService.changeCount(1L, Action.DELETE))
            .verifyComplete();

        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void shouldDoNothingWhenMinusActionAndCartItemNotFound() {
        when(cartItemRepository.findByItemId(1L))
            .thenReturn(Mono.empty());

        StepVerifier.create(cartService.changeCount(1L, Action.MINUS))
            .verifyComplete();

        verify(cartItemRepository, never()).save(any());
        verify(cartItemRepository, never()).delete(any());
    }

    @Test
    void shouldDoNothingWhenDeleteActionAndCartItemNotFound() {
        when(cartItemRepository.findByItemId(1L))
            .thenReturn(Mono.empty());

        StepVerifier.create(cartService.changeCount(1L, Action.DELETE))
            .verifyComplete();

        verify(cartItemRepository, never()).save(any());
        verify(cartItemRepository, never()).delete(any());
    }
}