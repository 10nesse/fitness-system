package com.fitness.repository;

import com.fitness.entity.Client;
import com.fitness.entity.Trainer;
import com.fitness.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    boolean existsByUser(User user);
    boolean existsByEmail(String email);
    // Добавлен метод для поиска тренера по username пользователя
    Optional<Trainer> findByUserUsername(String username);
}