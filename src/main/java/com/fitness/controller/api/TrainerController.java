// src/main/java/com/fitness/controller/TrainerController.java
package com.fitness.controller.api;

import com.fitness.entity.Trainer;
import com.fitness.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping
    public ResponseEntity<List<Trainer>> getAllTrainers() {
        List<Trainer> trainers = trainerService.getAllTrainers();
        return ResponseEntity.ok(trainers);
    }

    @PostMapping
    public ResponseEntity<Trainer> createTrainer(@RequestBody Trainer trainer) {
        Trainer createdTrainer = trainerService.createTrainer(trainer);
        return ResponseEntity.status(201).body(createdTrainer);
    }

    // Другие методы (если есть)
}
