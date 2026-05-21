package com.example.itemservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.itemservice.model.ReservationLog;
import com.example.itemservice.service.ItemServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMessageConsumer {

    private final ItemServiceImpl itemService;

    @KafkaListener(
            topics = "${app.kafka.topic.orders}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderPlaced(@Payload OrderMessage event) {
        log.info("[CONSUMER] Received event: {}", event);

        try {
            ReservationLog result = itemService.processReservation(event);
            log.info("[CONSUMER] Reservation result: status={} message={}",
                    result.getStatus(), result.getMessage());
        } catch (Exception ex) {
            log.error("[CONSUMER] Processing failed for orderId={}: {}",
                    event.getOrderId(), ex.getMessage(), ex);
            throw ex;
        }
    }
}
