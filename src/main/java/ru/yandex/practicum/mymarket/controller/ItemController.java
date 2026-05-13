package ru.yandex.practicum.mymarket.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.mymarket.dto.ChangeItemActionRequest;
import ru.yandex.practicum.mymarket.dto.ChangeItemRequest;
import ru.yandex.practicum.mymarket.enums.SortType;
import ru.yandex.practicum.mymarket.service.CartService;
import ru.yandex.practicum.mymarket.service.ItemService;
import ru.yandex.practicum.mymarket.validation.AllowedPageSize;

@Controller
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;
    private final CartService cartService;

    @GetMapping({"/", "/items"})
    public Mono<String> items(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "NO") SortType sort,
            @RequestParam(defaultValue = "1") @Positive Integer pageNumber,
            @RequestParam(defaultValue = "5") @AllowedPageSize Integer pageSize,
            Model model) {
        return itemService
                .getItemsPage(search, sort, pageNumber, pageSize)
                .map(
                        page -> {
                            model.addAttribute("items", page.items());
                            model.addAttribute("paging", page.paging());
                            model.addAttribute("search", search);
                            model.addAttribute("sort", sort);
                            return "items";
                        });
    }

    @PostMapping("/items")
    public Mono<String> changeFromItems(@ModelAttribute ChangeItemRequest request) {
        return cartService
                .changeCount(request.id(), request.action())
                .thenReturn(buildItemsRedirectUrl(request));
    }

    @GetMapping("/items/{id}")
    public Mono<String> item(@PathVariable Long id, Model model) {
        return itemService
                .getItem(id)
                .map(
                        item -> {
                            model.addAttribute("item", item);
                            return "item";
                        });
    }

    @PostMapping("/items/{id}")
    public Mono<String> changeFromItem(
            @PathVariable Long id, @ModelAttribute ChangeItemActionRequest request) {
        return cartService.changeCount(id, request.action()).thenReturn("redirect:/items/" + id);
    }

    private String buildItemsRedirectUrl(ChangeItemRequest request) {
        return UriComponentsBuilder.fromPath("/items")
                .queryParam("search", request.search())
                .queryParam("sort", request.sort())
                .queryParam("pageNumber", request.pageNumber())
                .queryParam("pageSize", request.pageSize())
                .build()
                .toUriString();
    }
}
