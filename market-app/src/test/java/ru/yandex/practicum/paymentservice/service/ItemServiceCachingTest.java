package ru.yandex.practicum.paymentservice.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import ru.yandex.practicum.paymentservice.repository.ItemRepository;

@SpringBootTest
@EnableCaching
class ItemServiceCachingTest {

    @Autowired
    private ItemService itemService;

    @MockitoSpyBean
    private ItemRepository itemRepository;

    @Test
    void shouldUseCacheForGetItem() {

        itemService.getItem(1L);
        itemService.getItem(1L);

        verify(itemRepository, times(1)).findById(1L);
    }
}
