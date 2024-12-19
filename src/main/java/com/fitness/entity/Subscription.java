package com.fitness.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) // Каскадное удаление клиента
    private Client client;

    @ManyToOne
    @JoinColumn(name = "fitness_class_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) // Каскадное удаление фитнес-класса
    private FitnessClass fitnessClass;

    @OneToOne(mappedBy = "subscription", cascade = CascadeType.ALL)
    @JsonBackReference
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE) // Каскадное удаление расписания
    private Schedule schedule;
}
