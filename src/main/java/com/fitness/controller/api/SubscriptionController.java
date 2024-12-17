// src/main/java/com/fitness/controller/SubscriptionController.java
package com.fitness.controller.api;

import com.fitness.entity.Subscription;
import com.fitness.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<Subscription> createSubscription(@RequestBody Subscription subscription) {
        Subscription savedSubscription = subscriptionService.saveSubscription(subscription);
        return ResponseEntity.ok(savedSubscription);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable Long id) {
        Optional<Subscription> subscriptionOpt = subscriptionService.getSubscriptionById(id);
        return subscriptionOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subscription> updateSubscription(@PathVariable Long id, @RequestBody Subscription subscriptionDetails) {
        Optional<Subscription> subscriptionOpt = subscriptionService.getSubscriptionById(id);
        if (subscriptionOpt.isPresent()) {
            Subscription subscription = subscriptionOpt.get();
            subscription.setStartDate(subscriptionDetails.getStartDate());
            subscription.setEndDate(subscriptionDetails.getEndDate());
            subscription.setPrice(subscriptionDetails.getPrice());
            subscription.setClient(subscriptionDetails.getClient());
            subscription.setFitnessClass(subscriptionDetails.getFitnessClass());
            // Обновить другие поля по необходимости
            Subscription updatedSubscription = subscriptionService.saveSubscription(subscription);
            return ResponseEntity.ok(updatedSubscription);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok("Subscription deleted successfully");
    }

    // Дополнительные конечные точки

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Subscription>> getSubscriptionsByClient(
            @PathVariable Long clientId
    ) {
        List<Subscription> subscriptions = subscriptionService.findByClientId(clientId);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Subscription>> getSubscriptionsByClass(
            @PathVariable Long classId
    ) {
        List<Subscription> subscriptions = subscriptionService.findByFitnessClassId(classId);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<Subscription>> getExpiredSubscriptions(
            @RequestParam String date
    ) {
        LocalDate localDate = LocalDate.parse(date);
        List<Subscription> subscriptions = subscriptionService.findExpiredSubscriptions(localDate);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Subscription>> getUpcomingSubscriptions(
            @RequestParam String date
    ) {
        LocalDate localDate = LocalDate.parse(date);
        List<Subscription> subscriptions = subscriptionService.findUpcomingSubscriptions(localDate);
        return ResponseEntity.ok(subscriptions);
    }
}
