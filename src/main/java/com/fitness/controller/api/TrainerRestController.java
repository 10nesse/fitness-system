package com.fitness.controller.api;

import com.fitness.entity.Trainer;
import com.fitness.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
public class TrainerRestController {

    private final TrainerService trainerService;

    public TrainerRestController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping
    public ResponseEntity<List<Trainer>> getAllTrainers() {
        List<Trainer> trainers = trainerService.getAllTrainers();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trainer> getTrainerById(@PathVariable Long id) {
        return trainerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Trainer> createTrainer(@RequestBody Trainer trainer) {
        Trainer createdTrainer = trainerService.createTrainer(trainer);
        return ResponseEntity.ok(createdTrainer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trainer> updateTrainer(@PathVariable Long id, @RequestBody Trainer updatedTrainer) {
        return trainerService.findById(id)
                .map(trainer -> {
                    trainer.setFirstName(updatedTrainer.getFirstName());
                    trainer.setLastName(updatedTrainer.getLastName());
                    trainer.setEmail(updatedTrainer.getEmail());
                    trainer.setPhoneNumber(updatedTrainer.getPhoneNumber());
                    Trainer savedTrainer = trainerService.saveTrainer(trainer);
                    return ResponseEntity.ok(savedTrainer);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        if (trainerService.findById(id).isPresent()) {
            trainerService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
