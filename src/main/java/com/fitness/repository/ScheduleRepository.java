package com.fitness.repository;

import com.fitness.entity.FitnessClass;
import com.fitness.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // Поиск по ID класса фитнеса
    List<Schedule> findByFitnessClass_Id(Long fitnessClassId);

    List<Schedule> findByFitnessClassIn(List<FitnessClass> fitnessClasses);


    // Поиск по периоду
    List<Schedule> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // Поиск по тренеру и периоду
    List<Schedule> findByFitnessClass_Trainer_IdAndStartTimeBetween(Long trainerId, LocalDateTime start, LocalDateTime end);
}
