// src/main/java/com/fitness/repository/PaymentRepository.java
package com.fitness.repository;

import com.fitness.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Поиск платежей по ID абонемента
    List<Payment> findBySubscription_Id(Long subscriptionId);

    List<Payment> findByClient_Id(Long clientId);

    // Поиск платежей по дате
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);

    // Поиск платежей по сумме больше заданной
    List<Payment> findByAmountGreaterThan(Double amount);

    // Подсчет платежей по ID абонемента
    long countBySubscription_Id(Long subscriptionId);

    // Поиск платежей по email клиента
    List<Payment> findBySubscription_Client_Email(String email);

}
