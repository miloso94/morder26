package com.example.itemservice.service;

import com.example.itemservice.kafka.OrderMessage;
import com.example.itemservice.model.ReservationLog;

public interface ItemService {

    public ReservationLog processReservation(OrderMessage event);

}
