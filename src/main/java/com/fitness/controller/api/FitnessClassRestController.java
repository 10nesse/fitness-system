package com.fitness.controller.api;

import com.fitness.entity.FitnessClass;
import com.fitness.service.FitnessClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fitness-classes")
public class FitnessClassRestController {

    private final FitnessClassService fitnessClassService;

    public FitnessClassRestController(FitnessClassService fitnessClassService) {
        this.fitnessClassService = fitnessClassService;
    }

    @GetMapping
    public ResponseEntity<List<FitnessClass>> getAllFitnessClasses() {
        List<FitnessClass> fitnessClasses = fitnessClassService.getAllFitnessClasses();
        return ResponseEntity.ok(fitnessClasses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FitnessClass> getFitnessClassById(@PathVariable Long id) {
        return fitnessClassService.getFitnessClassById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FitnessClass> createFitnessClass(@RequestBody FitnessClass fitnessClass) {
        FitnessClass createdClass = fitnessClassService.saveFitnessClass(fitnessClass);
        return ResponseEntity.ok(createdClass);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FitnessClass> updateFitnessClass(@PathVariable Long id, @RequestBody FitnessClass updatedClass) {
        return fitnessClassService.getFitnessClassById(id)
                .map(fitnessClass -> {
                    fitnessClass.setName(updatedClass.getName());
                    fitnessClass.setDescription(updatedClass.getDescription());
                    fitnessClass.setCapacity(updatedClass.getCapacity());
                    FitnessClass savedClass = fitnessClassService.saveFitnessClass(fitnessClass);
                    return ResponseEntity.ok(savedClass);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFitnessClass(@PathVariable Long id) {
        if (fitnessClassService.getFitnessClassById(id).isPresent()) {
            fitnessClassService.deleteFitnessClass(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
