package com.fitness.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

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

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({"user"})
    @OnDelete(action = OnDeleteAction.CASCADE) // При удалении Client удаляются Subscriptions
    private Client client;

    @ManyToOne
    @JoinColumn(name = "fitness_class_id", nullable = false)
    @JsonIgnoreProperties({"description", "capacity", "trainer", "schedules"})
    @OnDelete(action = OnDeleteAction.CASCADE) // При удалении FitnessClass удаляются Subscriptions
    private FitnessClass fitnessClass;

    @OneToOne(mappedBy = "subscription", cascade = CascadeType.ALL)
    @JsonBackReference
    // Здесь уже CascadeType.ALL для Payment
    private Payment payment;


    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = true)
    private Schedule schedule;
}
