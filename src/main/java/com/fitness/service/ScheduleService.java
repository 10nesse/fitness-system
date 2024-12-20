package com.fitness.service;

import com.fitness.dto.ScheduleWithCapacityDTO;
import com.fitness.dto.ScheduleWithClientsDTO;
import com.fitness.entity.*;
import com.fitness.repository.RegistrationRepository;
import com.fitness.repository.ScheduleRepository;
import com.fitness.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;


    @Autowired
    private RegistrationRepository registrationRepository;

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
        List<Schedule> schedules = scheduleRepository.findByFitnessClass_Trainer(trainer);

        return schedules.stream().map(schedule -> {
            List<Registration> registrations = registrationRepository.findBySchedule(schedule); // Извлекаем регистрации
            List<Client> clients = registrations.stream()
                    .map(Registration::getClient) // Получаем клиентов из регистраций
                    .toList();
            int registered = clients.size();
            int capacity = schedule.getFitnessClass().getCapacity();

            return new ScheduleWithClientsDTO(schedule, clients, registered, capacity);
        }).toList();
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

    public int getRegisteredClientsCount(Long scheduleId) {
        return (int) subscriptionRepository.countBySchedule_Id(scheduleId);
    }

    public List<ScheduleWithCapacityDTO> getSchedulesWithCapacity(List<Schedule> schedules) {
        return schedules.stream().map(schedule -> {
            int registeredCount = (int) registrationRepository.countBySchedule(schedule);
            int capacity = schedule.getFitnessClass().getCapacity();
            return new ScheduleWithCapacityDTO(schedule, registeredCount, capacity);
        }).collect(Collectors.toList());
    }

    public List<Schedule> findSchedulesByClient(Client client) {
        List<Registration> registrations = registrationRepository.findByClient(client);
        return registrations.stream()
                .map(Registration::getSchedule)
                .collect(Collectors.toList());
    }

    public List<Schedule> findSchedulesByFitnessClassesAndPeriod(List<FitnessClass> fitnessClasses, LocalDateTime start, LocalDateTime end) {
        return scheduleRepository.findByFitnessClassInAndStartTimeBetween(fitnessClasses, start, end);
    }






    public boolean registerClientToSchedule(Client client, Schedule schedule) {
        // Проверка, существует ли уже запись
        if (registrationRepository.existsByClientAndSchedule(client, schedule)) {
            throw new IllegalStateException("Вы уже записаны на это занятие.");
        }

        int registered = (int) registrationRepository.countBySchedule(schedule);
        int capacity = schedule.getFitnessClass().getCapacity();

        if (registered >= capacity) {
            throw new IllegalStateException("Нет доступных мест на это занятие.");
        }

        Registration registration = new Registration();
        registration.setClient(client);
        registration.setSchedule(schedule);

        registrationRepository.save(registration);
        return true;
    }







}
