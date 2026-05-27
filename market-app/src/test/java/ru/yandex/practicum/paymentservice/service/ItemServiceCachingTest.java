package ru.yandex.practicum.paymentservice.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import ru.yandex.practicum.paymentservice.repository.ItemRepository;
import ru.yandex.practicum.paymentservice.util.AbstractIntegrationTest;

@EnableCaching
class ItemServiceCachingTest extends AbstractIntegrationTest {

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
