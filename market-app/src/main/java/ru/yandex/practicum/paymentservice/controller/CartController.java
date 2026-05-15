package ru.yandex.practicum.paymentservice.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.paymentservice.enums.Action;
import ru.yandex.practicum.paymentservice.service.CartService;

@Controller
@RequestMapping("/cart/items")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String cart(Model model) {
        fillCartModel(model);
        return "cart";
    }

    @PostMapping
    public String changeCartItem(
        @RequestParam @Positive Long id, @RequestParam Action action, Model model) {
        cartService.changeCount(id, action);
        fillCartModel(model);
        return "redirect:/cart/items";
    }

    private void fillCartModel(Model model) {
        model.addAttribute("items", cartService.getCartItems());
        model.addAttribute("total", cartService.getTotal());
    }
}
