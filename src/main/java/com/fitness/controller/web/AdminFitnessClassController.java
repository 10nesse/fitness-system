// src/main/java/com/fitness/controller/web/WebFitnessClassController.java
package com.fitness.controller.web;

import com.fitness.entity.FitnessClass;
import com.fitness.entity.Trainer;
import com.fitness.service.FitnessClassService;
import com.fitness.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web/admin/fitness-classes")
public class AdminFitnessClassController {

    private final FitnessClassService fitnessClassService;
    private final TrainerService trainerService;

    @Autowired
    public AdminFitnessClassController(FitnessClassService fitnessClassService, TrainerService trainerService) {
        this.fitnessClassService = fitnessClassService;
        this.trainerService = trainerService;
    }

    // Список всех фитнес-классов
    @GetMapping
    public String listFitnessClasses(Model model) {
        List<FitnessClass> fitnessClasses = fitnessClassService.getAllFitnessClasses();
        model.addAttribute("fitnessClasses", fitnessClasses);
        return "admin/fitness-classes"; // Шаблон для отображения списка
    }

    // Форма создания нового фитнес-класса
    @GetMapping("/create")
    public String createFitnessClassForm(Model model) {
        model.addAttribute("fitnessClass", new FitnessClass());
        List<Trainer> trainers = trainerService.getAllTrainers();
        model.addAttribute("trainers", trainers); // Список тренеров для выбора
        return "admin/create-fitness-class"; // Шаблон для создания
    }

    // Обработка создания фитнес-класса
    @PostMapping("/create")
    public String createFitnessClass(
            @Valid @ModelAttribute("fitnessClass") FitnessClass fitnessClass,
            BindingResult bindingResult,
            @RequestParam("trainerId") Long trainerId,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("trainers", trainerService.getAllTrainers());
            return "admin/create-fitness-class";
        }

        Optional<Trainer> trainerOpt = trainerService.findById(trainerId);
        if (trainerOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Trainer not found");
            model.addAttribute("trainers", trainerService.getAllTrainers());
            return "admin/create-fitness-class";
        }

        fitnessClass.setTrainer(trainerOpt.get());
        fitnessClassService.saveFitnessClass(fitnessClass);

        return "redirect:/web/admin/fitness-classes";
    }

    // Форма редактирования фитнес-класса
    @GetMapping("/edit/{id}")
    public String editFitnessClassForm(@PathVariable Long id, Model model) {
        FitnessClass existingClass = fitnessClassService.getFitnessClassById(id)
                .orElseThrow(() -> new RuntimeException("Fitness class not found"));
        model.addAttribute("fitnessClass", existingClass);
        model.addAttribute("trainers", trainerService.getAllTrainers());
        return "admin/edit-fitness-class"; // Шаблон для редактирования
    }

    // Обработка обновления фитнес-класса
    @PostMapping("/edit")
    public String updateFitnessClass(
            @Valid @ModelAttribute("fitnessClass") FitnessClass fitnessClass,
            BindingResult bindingResult,
            @RequestParam("trainerId") Long trainerId,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("trainers", trainerService.getAllTrainers());
            return "admin/edit-fitness-class";
        }

        Optional<Trainer> trainerOpt = trainerService.findById(trainerId);
        if (trainerOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Trainer not found");
            model.addAttribute("trainers", trainerService.getAllTrainers());
            return "admin/edit-fitness-class";
        }

        // Убедитесь, что фитнес-класс существует
        FitnessClass existingClass = fitnessClassService.getFitnessClassById(fitnessClass.getId())
                .orElseThrow(() -> new RuntimeException("Fitness class not found"));

        existingClass.setName(fitnessClass.getName());
        existingClass.setDescription(fitnessClass.getDescription());
        existingClass.setCapacity(fitnessClass.getCapacity());
        existingClass.setTrainer(trainerOpt.get());

        fitnessClassService.saveFitnessClass(existingClass);

        return "redirect:/web/admin/fitness-classes";
    }

    // Удаление фитнес-класса
    @GetMapping("/delete/{id}")
    public String deleteFitnessClass(@PathVariable Long id) {
        fitnessClassService.deleteFitnessClass(id);
        return "redirect:/web/admin/fitness-classes";
    }
}
