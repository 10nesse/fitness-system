package com.fitness.dto;

import com.fitness.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleWithCapacityDTO {
    private Schedule schedule;
    private int registeredCount; // Количество зарегистрированных клиентов
    private int capacity;        // Общая вместимость (неизменная)
}

