// src/main/java/com/fitness/service/FitnessClassService.java
package com.fitness.service;

import com.fitness.entity.FitnessClass;
import com.fitness.entity.Trainer;
import com.fitness.repository.FitnessClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FitnessClassService {

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    /**
     * Сохранение или обновление фитнес-класса
     */
    public FitnessClass saveFitnessClass(FitnessClass fitnessClass) {
        return fitnessClassRepository.save(fitnessClass);
    }

    // Поиск фитнес-классов по тренеру
    public List<FitnessClass> findByTrainer(Trainer trainer) {
        return fitnessClassRepository.findByTrainer(trainer);
    }


    /**
     * Получение всех фитнес-классов тренера по username
     */
    public List<FitnessClass> getAllFitnessClassesByTrainerUsername(String username) {
        return fitnessClassRepository.findByTrainerUserUsername(username);
    }

    /**
     * Поиск первого фитнес-класса тренера по username
     */
    public Optional<FitnessClass> findFirstFitnessClassByTrainerUsername(String username) {
        return fitnessClassRepository.findByTrainerUserUsername(username).stream().findFirst();
    }


    /**
     * Поиск фитнес-класса по ID и тренеру
     */
    public Optional<FitnessClass> findByIdAndTrainer(Long id, Trainer trainer) {
        return fitnessClassRepository.findByIdAndTrainer(id, trainer);
    }

    /**
     * Получение фитнес-класса по ID
     */
    public Optional<FitnessClass> getFitnessClassById(Long id) {
        return fitnessClassRepository.findById(id);
    }

    /**
     * Получение всех фитнес-классов
     */
    public List<FitnessClass> getAllFitnessClasses() {
        return fitnessClassRepository.findAll();
    }

    /**
     * Удаление фитнес-класса по ID
     */
    public void deleteFitnessClass(Long id) {
        fitnessClassRepository.deleteById(id);
    }

    public List<FitnessClass> searchByName(String name) {
        return fitnessClassRepository.findByNameContaining(name);
    }

    public List<FitnessClass> findByTrainerId(Long trainerId) {
        return fitnessClassRepository.findByTrainer_Id(trainerId);
    }

    public List<FitnessClass> findByCapacity(Integer capacity) {
        return fitnessClassRepository.findByCapacityGreaterThanEqual(capacity);
    }

    public List<FitnessClass> findClassesWithinPeriod(LocalDateTime start, LocalDateTime end) {
        return fitnessClassRepository.findDistinctBySchedules_StartTimeBetween(start, end);
    }
}
