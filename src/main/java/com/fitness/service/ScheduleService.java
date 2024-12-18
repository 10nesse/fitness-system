package com.fitness.service;

import com.fitness.entity.FitnessClass;
import com.fitness.entity.Schedule;
import com.fitness.entity.Subscription;
import com.fitness.entity.Trainer;
import com.fitness.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    public Schedule saveSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    public List<Schedule> findByFitnessClassId(Long fitnessClassId) {
        return scheduleRepository.findByFitnessClass_Id(fitnessClassId);

    }

    // Поиск расписаний по фитнес-классам
    public List<Schedule> findByFitnessClasses(List<FitnessClass> fitnessClasses) {
        return scheduleRepository.findByFitnessClassIn(fitnessClasses);
    }

    // Поиск расписания по ID
    public Optional<Schedule> findById(Long id) {
        return scheduleRepository.findById(id);
    }

    // Удаление расписания по ID
    public void deleteById(Long id) {
        scheduleRepository.deleteById(id);
    }

    // Поиск расписания по ID и тренеру
    public Optional<Schedule> findByIdAndTrainer(Long id, Trainer trainer) {
        return scheduleRepository.findByIdAndFitnessClass_Trainer(id, trainer);
    }

    // Поиск всех расписаний тренера
    public List<Schedule> findByTrainer(Trainer trainer) {
        return scheduleRepository.findByFitnessClass_Trainer(trainer);
    }

    public List<Schedule> findSchedulesBySubscriptions(List<Subscription> subscriptions) {
        // Извлекаем фитнес-классы из абонементов
        List<FitnessClass> fitnessClasses = subscriptions.stream()
                .map(Subscription::getFitnessClass)
                .collect(Collectors.toList());

        // Находим расписания для этих фитнес-классов
        return scheduleRepository.findByFitnessClassIn(fitnessClasses);
    }

    public List<Schedule> findByPeriod(LocalDateTime start, LocalDateTime end) {
        return scheduleRepository.findByStartTimeBetween(start, end);
    }

    public List<Schedule> findByTrainerAndPeriod(Long trainerId, LocalDateTime start, LocalDateTime end) {
        return scheduleRepository.findByFitnessClass_Trainer_IdAndStartTimeBetween(trainerId, start, end);
    }
}
