package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.kafka.OrderMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final KafkaTemplate<String, OrderMessage> kafkaTemplate;

    @Value("${app.kafka.topic.orders}")
    private String ordersTopic;

    public void orderService(OrderRequest request) {

        OrderMessage msg = OrderMessage.builder()
                .orderId(request.getOrderId())
                .itemId(request.getItemId())
                .quantity(request.getQuantity())
                .createdAt(Instant.now())
                .build();

        log.info("[OrderService] Sending OrderMessage to topic '{}': {}", ordersTopic, msg);

        CompletableFuture<SendResult<String, OrderMessage>> future =
                kafkaTemplate.send(ordersTopic, msg.getOrderId(), msg);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[OrderService] Failed to publish message for orderId={}: {}", msg.getOrderId(), ex.getMessage());
            } else {
                log.info("[OrderService] Message published successfully to topic={}",
                        result.getRecordMetadata().topic());
            }
        });
    }
}
