// src/main/java/com/fitness/controller/PaymentController.java
package com.fitness.controller.api;

import com.fitness.entity.Payment;
import com.fitness.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        Payment savedPayment = paymentService.savePayment(payment);
        return ResponseEntity.ok(savedPayment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Optional<Payment> paymentOpt = paymentService.getPaymentById(id);
        return paymentOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment paymentDetails) {
        Optional<Payment> paymentOpt = paymentService.getPaymentById(id);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setPaymentDate(paymentDetails.getPaymentDate());
            payment.setAmount(paymentDetails.getAmount());
            payment.setSubscription(paymentDetails.getSubscription());
            // Обновить другие поля по необходимости
            Payment updatedPayment = paymentService.savePayment(payment);
            return ResponseEntity.ok(updatedPayment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok("Payment deleted successfully");
    }

    // Дополнительные конечные точки

    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<Payment>> getPaymentsBySubscription(
            @PathVariable Long subscriptionId
    ) {
        List<Payment> payments = paymentService.findBySubscriptionId(subscriptionId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Payment>> getPaymentsByDateRange(
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDateTime startDateTime = LocalDateTime.parse(start);
        LocalDateTime endDateTime = LocalDateTime.parse(end);
        List<Payment> payments = paymentService.findByDateRange(startDateTime, endDateTime);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/amount-greater")
    public ResponseEntity<List<Payment>> getPaymentsByAmountGreaterThan(
            @RequestParam Double amount
    ) {
        List<Payment> payments = paymentService.findByAmountGreaterThan(amount);
        return ResponseEntity.ok(payments);
    }
}
