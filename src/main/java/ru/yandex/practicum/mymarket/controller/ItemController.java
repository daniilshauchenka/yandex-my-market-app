package ru.yandex.practicum.mymarket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.mymarket.enums.Action;
import ru.yandex.practicum.mymarket.enums.SortType;
import ru.yandex.practicum.mymarket.service.ItemService;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @GetMapping({"/", "/items"})
    public String items(
        @RequestParam(defaultValue = "") String search,
        @RequestParam(defaultValue = "NO") SortType sort,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "5") int pageSize,
        Model model
    ) {
        model.addAttribute("items", itemService.getItems(search, sort, pageNumber, pageSize));
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", itemService.getPaging(search, sort, pageNumber, pageSize));
        return "items";
    }

    @PostMapping("/items")
    public String changeFromItems(
        @RequestParam long id,
        @RequestParam(defaultValue = "") String search,
        @RequestParam(defaultValue = "NO") SortType sort,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "5") int pageSize,
        @RequestParam Action action
    ) {
        itemService.changeCount(id, action);

        return "redirect:/items?search=" + search
            + "&sort=" + sort
            + "&pageNumber=" + pageNumber
            + "&pageSize=" + pageSize;
    }

    @GetMapping("/items/{id}")
    public String item(@PathVariable long id, Model model) {
        model.addAttribute("item", itemService.getItem(id));
        return "item";
    }

    @PostMapping("/items/{id}")
    public String changeFromItem(
        @PathVariable long id,
        @RequestParam Action action,
        Model model
    ) {
        itemService.changeCount(id, action);
        model.addAttribute("item", itemService.getItem(id));
        return "item";
    }
}
