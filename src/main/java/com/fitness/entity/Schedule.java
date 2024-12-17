package com.fitness.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule")
@Data
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "fitness_class_id", nullable = false)
    @JsonIgnoreProperties({"description", "capacity", "trainer", "schedules"})
    @OnDelete(action = OnDeleteAction.CASCADE) // При удалении FitnessClass удаляются Schedules
    private FitnessClass fitnessClass;
}
