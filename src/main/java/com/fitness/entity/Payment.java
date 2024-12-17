package com.fitness.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime paymentDate;
    private Double amount;

    @OneToOne
    @JoinColumn(name = "subscription_id", nullable = false)
    @JsonIgnoreProperties({"client", "fitnessClass"})
    @OnDelete(action = OnDeleteAction.CASCADE) // При удалении Subscription удаляется Payment
    private Subscription subscription;
}
