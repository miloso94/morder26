package com.example.itemservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.itemservice.model.ReservationLog;

@Repository
public interface ReservationLogRepository extends JpaRepository<ReservationLog, Long> {
    
}
