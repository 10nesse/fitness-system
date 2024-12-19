package com.fitness.dto;

import com.fitness.entity.Schedule;
import com.fitness.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleWithClientsDTO {
    private Schedule schedule;
    private List<Client> clients;
    private int registered;
    private int capacity;
}
