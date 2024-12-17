package com.fitness.repository;

import com.fitness.entity.Trainer;
import com.fitness.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    boolean existsByUser(User user);
    boolean existsByEmail(String email);
}