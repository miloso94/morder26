package com.example.itemservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.itemservice.kafka.OrderMessage;
import com.example.itemservice.model.Item;
import com.example.itemservice.model.ReservationLog;
import com.example.itemservice.model.ReservationLog.ReservationStatus;
import com.example.itemservice.repository.ItemRepository;
import com.example.itemservice.repository.ReservationLogRepository;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ReservationLogRepository reservationLogRepository;

    @Transactional
    public ReservationLog processReservation(OrderMessage event) {
        log.info("[ItemService] Processing reservation for orderId={} itemId={} qty={}",
                event.getOrderId(), event.getItemId(), event.getQuantity());


        Optional<Item> itemOpt =
                itemRepository.findByItemIdWithLock(event.getItemId());

        if (itemOpt.isEmpty()) {
            log.warn("[ItemService] Item '{}' not found in item", event.getItemId());
            return saveLog(event, ReservationStatus.ITEM_NOT_FOUND,
                    "Item '" + event.getItemId() + "' does not exist in item");
        }

        Item item = itemOpt.get();
        int available = item.getAvailableQuantity();
        int requested = event.getQuantity();

        if (available >= requested) {
            item.setAvailableQuantity(available - requested);
            itemRepository.save(item);

            String msg = String.format(
                    "Reserved %d unit(s) of '%s'.",
                    requested, event.getItemId(), available, item.getAvailableQuantity());
            log.info("[ItemService] {}", msg);
            return saveLog(event, ReservationStatus.RESERVED, msg);
        } else {
            String msg = String.format(
                    "Insufficient stock for '%s': requested=%d, available=%d",
                    event.getItemId(), requested, available);
            log.warn("[ItemService] {}", msg);
            return saveLog(event, ReservationStatus.INSUFFICIENT, msg);
        }
    }

    private ReservationLog saveLog(OrderMessage event, ReservationStatus status, String message) {
        ReservationLog log = ReservationLog.builder()
                .orderId(event.getOrderId())
                .itemId(event.getItemId())
                .requestedQuantity(event.getQuantity())
                .status(status)
                .message(message)
                .processedAt(Instant.now())
                .build();
        return reservationLogRepository.save(log);
    }

}
