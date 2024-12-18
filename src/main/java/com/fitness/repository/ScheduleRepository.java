package com.fitness.repository;

import com.fitness.entity.FitnessClass;
import com.fitness.entity.Schedule;
import com.fitness.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // Поиск по ID класса фитнеса
    List<Schedule> findByFitnessClass_Id(Long fitnessClassId);

    List<Schedule> findByFitnessClassIn(List<FitnessClass> fitnessClasses);

    // Поиск расписания по ID и тренеру
    Optional<Schedule> findByIdAndFitnessClass_Trainer(Long id, Trainer trainer);

    // Поиск всех расписаний тренера
    List<Schedule> findByFitnessClass_Trainer(Trainer trainer);

    List<Schedule> findByFitnessClass(FitnessClass fitnessClass);



    // Поиск по периоду
    List<Schedule> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // Поиск по тренеру и периоду
    List<Schedule> findByFitnessClass_Trainer_IdAndStartTimeBetween(Long trainerId, LocalDateTime start, LocalDateTime end);
}
