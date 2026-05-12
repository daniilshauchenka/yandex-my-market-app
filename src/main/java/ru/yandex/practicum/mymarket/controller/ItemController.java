package ru.yandex.practicum.mymarket.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.mymarket.enums.Action;
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
  public String items(
      @RequestParam(defaultValue = "") String search,
      @RequestParam(defaultValue = "NO") SortType sort,
      @RequestParam(defaultValue = "1") @Positive Integer pageNumber,
      @RequestParam(defaultValue = "5") @AllowedPageSize Integer pageSize,
      Model model) {
    model.addAttribute("items", itemService.getItems(search, sort, pageNumber, pageSize));
    model.addAttribute("paging", itemService.getPaging(search, sort, pageNumber, pageSize));
    model.addAttribute("search", search);
    model.addAttribute("sort", sort);
    return "items";
  }

  @PostMapping("/items")
  public String changeFromItems(
      @RequestParam Long id,
      @RequestParam Action action,
      @RequestParam(defaultValue = "") String search,
      @RequestParam(defaultValue = "NO") SortType sort,
      @RequestParam(defaultValue = "1") @Positive Integer pageNumber,
      @RequestParam(defaultValue = "5") @AllowedPageSize Integer pageSize) {
    cartService.changeCount(id, action);
    return "redirect:/items?search="
        + search
        + "&sort="
        + sort
        + "&pageNumber="
        + pageNumber
        + "&pageSize="
        + pageSize;
  }

  @GetMapping("/items/{id}")
  public String item(@PathVariable Long id, Model model) {
    model.addAttribute("item", itemService.getItem(id));
    return "item";
  }

  @PostMapping("/items/{id}")
  public String changeFromItem(@PathVariable Long id, @RequestParam Action action, Model model) {
    cartService.changeCount(id, action);
    model.addAttribute("item", itemService.getItem(id));
    return "redirect:/items/" + id;
  }
}
