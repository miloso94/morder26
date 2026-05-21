package com.example.itemservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage {

    private String orderId;
    private String itemId;
    private Integer quantity;
    private Instant createdAt;
}
