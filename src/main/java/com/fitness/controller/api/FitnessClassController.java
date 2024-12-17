// src/main/java/com/fitness/controller/FitnessClassController.java
package com.fitness.controller.api;

import com.fitness.entity.FitnessClass;
import com.fitness.service.FitnessClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/classes")
public class FitnessClassController {

    @Autowired
    private FitnessClassService fitnessClassService;

    @PostMapping
    public ResponseEntity<FitnessClass> createFitnessClass(@RequestBody FitnessClass fitnessClass) {
        FitnessClass savedClass = fitnessClassService.saveFitnessClass(fitnessClass);
        return ResponseEntity.ok(savedClass);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FitnessClass> getFitnessClassById(@PathVariable Long id) {
        Optional<FitnessClass> fitnessClassOpt = fitnessClassService.getFitnessClassById(id);
        return fitnessClassOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<FitnessClass>> getAllFitnessClasses() {
        List<FitnessClass> classes = fitnessClassService.getAllFitnessClasses();
        return ResponseEntity.ok(classes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FitnessClass> updateFitnessClass(@PathVariable Long id, @RequestBody FitnessClass classDetails) {
        Optional<FitnessClass> classOpt = fitnessClassService.getFitnessClassById(id);
        if (classOpt.isPresent()) {
            FitnessClass fitnessClass = classOpt.get();
            fitnessClass.setName(classDetails.getName());
            fitnessClass.setDescription(classDetails.getDescription());
            fitnessClass.setCapacity(classDetails.getCapacity());
            fitnessClass.setTrainer(classDetails.getTrainer());
            // Обновить другие поля по необходимости
            FitnessClass updatedClass = fitnessClassService.saveFitnessClass(fitnessClass);
            return ResponseEntity.ok(updatedClass);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFitnessClass(@PathVariable Long id) {
        fitnessClassService.deleteFitnessClass(id);
        return ResponseEntity.ok("Fitness class deleted successfully");
    }

    // Дополнительные конечные точки

    @GetMapping("/search")
    public ResponseEntity<List<FitnessClass>> searchFitnessClassesByName(
            @RequestParam String name
    ) {
        List<FitnessClass> classes = fitnessClassService.searchByName(name);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<FitnessClass>> getClassesByTrainer(
            @PathVariable Long trainerId
    ) {
        List<FitnessClass> classes = fitnessClassService.findByTrainerId(trainerId);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/capacity")
    public ResponseEntity<List<FitnessClass>> getClassesByCapacity(
            @RequestParam Integer capacity
    ) {
        List<FitnessClass> classes = fitnessClassService.findByCapacity(capacity);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/period")
    public ResponseEntity<List<FitnessClass>> getClassesWithinPeriod(
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDateTime startDateTime = LocalDateTime.parse(start);
        LocalDateTime endDateTime = LocalDateTime.parse(end);
        List<FitnessClass> classes = fitnessClassService.findClassesWithinPeriod(startDateTime, endDateTime);
        return ResponseEntity.ok(classes);
    }
}
