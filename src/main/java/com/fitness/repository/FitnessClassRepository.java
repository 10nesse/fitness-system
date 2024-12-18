// src/main/java/com/fitness/repository/FitnessClassRepository.java
package com.fitness.repository;

import com.fitness.entity.FitnessClass;
import com.fitness.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FitnessClassRepository extends JpaRepository<FitnessClass, Long> {
    List<FitnessClass> findByNameContaining(String name);
    List<FitnessClass> findByTrainer_Id(Long trainerId);
    List<FitnessClass> findByCapacityGreaterThanEqual(Integer capacity);
    List<FitnessClass> findDistinctBySchedules_StartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<FitnessClass> findByTrainer(Trainer trainer);
    Optional<FitnessClass> findByIdAndTrainer(Long id, Trainer trainer);
}
