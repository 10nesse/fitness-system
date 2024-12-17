// src/main/java/com/fitness/service/SubscriptionService.java
package com.fitness.service;

import com.fitness.entity.Subscription;
import com.fitness.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public Subscription saveSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public void deleteSubscription(Long id) {
        subscriptionRepository.deleteById(id);
    }

    public List<Subscription> findByClientId(Long clientId) {
        return subscriptionRepository.findByClient_Id(clientId);
    }

    public List<Subscription> findByClientEmail(String email) {
        return subscriptionRepository.findByClient_Email(email);
    }

    public List<Subscription> findByFitnessClassId(Long fitnessClassId) {
        return subscriptionRepository.findByFitnessClass_Id(fitnessClassId);
    }

    public List<Subscription> findExpiredSubscriptions(LocalDate date) {
        return subscriptionRepository.findByEndDateBefore(date);
    }

    public List<Subscription> findUpcomingSubscriptions(LocalDate date) {
        return subscriptionRepository.findByStartDateAfter(date);
    }

    public long countByClientId(Long clientId) {
        return subscriptionRepository.countByClient_Id(clientId);
    }
}
