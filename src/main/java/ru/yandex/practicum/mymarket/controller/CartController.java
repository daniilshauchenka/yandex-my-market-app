package ru.yandex.practicum.mymarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.ChangeCartItemRequest;
import ru.yandex.practicum.mymarket.service.CartService;

@Controller
@RequestMapping("/cart/items")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Mono<String> cart(Model model) {
        return Mono.zip(cartService.getCartItems(), cartService.getTotal())
                .map(
                        tuple -> {
                            model.addAttribute("items", tuple.getT1());
                            model.addAttribute("total", tuple.getT2());
                            return "cart";
                        });
    }

    @PostMapping
    public Mono<String> changeCartItem(@ModelAttribute ChangeCartItemRequest request) {
        return cartService
                .changeCount(request.id(), request.action())
                .thenReturn("redirect:/cart/items");
    }
}
