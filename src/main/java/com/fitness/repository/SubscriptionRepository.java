package com.fitness.repository;
import com.fitness.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByClient_Id(Long clientId);

    List<Subscription> findByClient_Email(String email);

    List<Subscription> findByFitnessClass_Id(Long fitnessClassId);

    List<Subscription> findByEndDateBefore(LocalDate endDate);

    List<Subscription> findByStartDateAfter(LocalDate startDate);

    long countByClient_Id(Long clientId);


    long countBySchedule_Id(Long scheduleId);


}
