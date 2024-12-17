// src/main/java/com/fitness/service/PaymentService.java
package com.fitness.service;

import com.fitness.entity.Payment;
import com.fitness.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Сохранение платежа
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Получение платежа по ID
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    // Получение всех платежей
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Удаление платежа по ID
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    // Поиск платежей по ID абонемента
    public List<Payment> findBySubscriptionId(Long subscriptionId) {
        return paymentRepository.findBySubscription_Id(subscriptionId);
    }

    // Поиск платежей по диапазону дат
    public List<Payment> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByPaymentDateBetween(start, end);
    }

    // Поиск платежей по сумме больше заданной
    public List<Payment> findByAmountGreaterThan(Double amount) {
        return paymentRepository.findByAmountGreaterThan(amount);
    }

    // Подсчет платежей по ID абонемента
    public long countBySubscriptionId(Long subscriptionId) {
        return paymentRepository.countBySubscription_Id(subscriptionId);
    }

    // Поиск платежей по Client.id
    public List<Payment> findByClientId(Long clientId) {
        return paymentRepository.findByClient_Id(clientId);
    }

    // Создание платежа
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Поиск платежей по email клиента
    public List<Payment> findByClientEmail(String email) {
        return paymentRepository.findBySubscription_Client_Email(email);
    }

    // Получение общего дохода
    public Double getTotalRevenue() {
        return paymentRepository.findAll()
                .stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }




    // Дополнительные методы по необходимости
}
