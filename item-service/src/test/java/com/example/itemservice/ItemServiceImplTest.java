package com.example.itemservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.itemservice.kafka.OrderMessage;
import com.example.itemservice.model.Item;
import com.example.itemservice.model.ReservationLog;
import com.example.itemservice.model.ReservationLog.ReservationStatus;
import com.example.itemservice.repository.ItemRepository;
import com.example.itemservice.repository.ReservationLogRepository;
import com.example.itemservice.service.ItemServiceImpl;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ReservationLogRepository reservationLogRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private OrderMessage order;

    @BeforeEach
    void setUp() {
        order = new OrderMessage();
        order.setOrderId("123");
        order.setItemId("item-1");
        order.setQuantity(2);
    }

    @Test
    void testSufficientStock() {

        Item item = new Item();
        item.setItemId("item-1");
        item.setAvailableQuantity(10);

        when(itemRepository.findByItemIdWithLock("item-1")).thenReturn(Optional.of(item));
        when(reservationLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservationLog result = itemService.processReservation(order);

        assertEquals(ReservationStatus.RESERVED, result.getStatus());
        assertEquals(8, item.getAvailableQuantity());
        verify(itemRepository).save(item);
    }

    @Test
    void testStockIsLow() {
        
        Item item = new Item();
        item.setItemId("item-1");
        item.setAvailableQuantity(1);

        when(itemRepository.findByItemIdWithLock("item-1")).thenReturn(Optional.of(item));
        when(reservationLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservationLog result = itemService.processReservation(order);

        assertEquals(ReservationStatus.INSUFFICIENT, result.getStatus());
        assertEquals(1, item.getAvailableQuantity()); 
        verify(itemRepository, never()).save(any());
    }

    @Test
    void testItemDoesNotExist() {
        
        when(itemRepository.findByItemIdWithLock("item-1")).thenReturn(Optional.empty());
        when(reservationLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservationLog result = itemService.processReservation(order);

        assertEquals(ReservationStatus.ITEM_NOT_FOUND, result.getStatus());
        verify(itemRepository, never()).save(any());
    }

}
