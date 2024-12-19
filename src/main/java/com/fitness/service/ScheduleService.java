package com.fitness.service;

import com.fitness.dto.ScheduleWithClientsDTO;
import com.fitness.entity.*;
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

    @Autowired
    private FitnessClassService fitnessClassService;

    /**
     * Сохранение или обновление расписания
     */
    public Schedule saveSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    /**
     * Получение расписания по ID
     */
    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    /**
     * Получение всех расписаний
     */
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    /**
     * Удаление расписания по ID
     */
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    public List<Schedule> findByFitnessClassId(Long fitnessClassId) {
        return scheduleRepository.findByFitnessClass_Id(fitnessClassId);

    }



    public List<Schedule> findByFitnessClass(FitnessClass fitnessClass) {
        return scheduleRepository.findByFitnessClass(fitnessClass);
    }

    /**
     * Получение расписаний с клиентами для тренера
     */
    public List<ScheduleWithClientsDTO> getAllSchedulesWithClients(Trainer trainer) {
        List<FitnessClass> fitnessClasses = fitnessClassService.findByTrainer(trainer);
        List<Schedule> schedules = scheduleRepository.findByFitnessClassIn(fitnessClasses);

        return schedules.stream()
                .map(schedule -> {
                    List<Client> clients = schedule.getSubscriptions().stream()
                            .map(Subscription::getClient)
                            .collect(Collectors.toList());
                    int registered = clients.size();
                    int capacity = schedule.getFitnessClass().getCapacity();
                    return new ScheduleWithClientsDTO(schedule, clients, registered, capacity);
                })
                .collect(Collectors.toList());
    }



    public List<Schedule> findSchedulesByFitnessClasses(List<FitnessClass> fitnessClasses) {
        return scheduleRepository.findByFitnessClassIn(fitnessClasses);
    }

    /**
     * Получение расписаний для списка фитнес-классов
     */
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
