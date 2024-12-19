package com.fitness.repository;

import com.fitness.entity.Client;
import com.fitness.entity.Registration;
import com.fitness.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    boolean existsByClientAndSchedule(Client client, Schedule schedule);

    long countBySchedule(Schedule schedule);

    List<Registration> findByClient(Client client);

    // Поиск всех регистраций для заданного занятия
    List<Registration> findBySchedule(Schedule schedule);


}
