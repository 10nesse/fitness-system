package com.fitness.controller.api;

import com.fitness.entity.Subscription;
import com.fitness.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionRestController {

    private final SubscriptionService subscriptionService;

    public SubscriptionRestController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable Long id) {
        return subscriptionService.getSubscriptionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Subscription> createSubscription(@RequestBody Subscription subscription) {
        Subscription createdSubscription = subscriptionService.saveSubscription(subscription);
        return ResponseEntity.ok(createdSubscription);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subscription> updateSubscription(@PathVariable Long id, @RequestBody Subscription updatedSubscription) {
        return subscriptionService.getSubscriptionById(id)
                .map(subscription -> {
                    subscription.setStartDate(updatedSubscription.getStartDate());
                    subscription.setEndDate(updatedSubscription.getEndDate());
                    subscription.setPrice(updatedSubscription.getPrice());
                    subscription.setClient(updatedSubscription.getClient());
                    subscription.setFitnessClass(updatedSubscription.getFitnessClass());
                    subscription.setSchedule(updatedSubscription.getSchedule());
                    Subscription savedSubscription = subscriptionService.saveSubscription(subscription);
                    return ResponseEntity.ok(savedSubscription);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        if (subscriptionService.getSubscriptionById(id).isPresent()) {
            subscriptionService.deleteSubscription(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
