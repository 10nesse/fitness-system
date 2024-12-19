package com.fitness.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "trainers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user"})

public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Не каскадируем и не удаляем User при удалении Trainer

    private String specialization;
    private String email;
    private String phoneNumber;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FitnessClass> fitnessClasses;
}
