// src/main/java/com/fitness/controller/web/TrainerFitnessClassController.java
package com.fitness.controller.web.trainer;

import com.fitness.entity.FitnessClass;
import com.fitness.entity.Trainer;
import com.fitness.service.FitnessClassService;
import com.fitness.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web/trainer/classes")
public class TrainerFitnessClassController {

    private final FitnessClassService fitnessClassService;
    private final TrainerService trainerService;

    @Autowired
    public TrainerFitnessClassController(FitnessClassService fitnessClassService,
                                         TrainerService trainerService) {
        this.fitnessClassService = fitnessClassService;
        this.trainerService = trainerService;
    }

    /**
     * Просмотр всех фитнес-классов тренера
     */
    @GetMapping
    public String viewClasses(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<FitnessClass> fitnessClasses = fitnessClassService.getAllFitnessClassesByTrainerUsername(username);
        model.addAttribute("fitnessClasses", fitnessClasses);
        return "trainer/classes";
    }

    /**
     * Показать форму для создания нового фитнес-класса
     */
    @GetMapping("/create")
    public String showCreateClassForm(Model model) {
        model.addAttribute("fitnessClass", new FitnessClass());
        return "trainer/create-class";
    }

    /**
     * Обработка создания нового фитнес-класса
     */
    @PostMapping("/create")
    public String createFitnessClass(@Valid @ModelAttribute("fitnessClass") FitnessClass fitnessClass,
                                     BindingResult bindingResult,
                                     Authentication authentication,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            return "trainer/create-class"; // Возвращает форму с объектом fitnessClass и ошибками
        }

        String username = authentication.getName();
        Optional<Trainer> trainerOpt = trainerService.findByUserUsername(username);

        if (trainerOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Тренер не найден");
            return "trainer/classes";
        }

        Trainer trainer = trainerOpt.get();
        fitnessClass.setTrainer(trainer);
        fitnessClassService.saveFitnessClass(fitnessClass);

        return "redirect:/web/trainer/classes";
    }

    // Остальные методы (редактирование, удаление) остаются без изменений

    /**
     * Показать форму для редактирования существующего фитнес-класса
     */
    @GetMapping("/edit/{id}")
    public String showEditClassForm(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<FitnessClass> fitnessClassOpt = fitnessClassService.getFitnessClassById(id);

        if (fitnessClassOpt.isEmpty()) {
            // Можно перенаправить на страницу 404 или отобразить сообщение об ошибке
            return "redirect:/error/404";
        }

        FitnessClass fitnessClass = fitnessClassOpt.get();

        // Проверка, что текущий пользователь является владельцем класса
        String username = authentication.getName();
        if (!fitnessClass.getTrainer().getUser().getUsername().equals(username)) {
            // Перенаправить на страницу 403 (доступ запрещён) или другую страницу
            return "redirect:/error/403";
        }

        model.addAttribute("fitnessClass", fitnessClass);
        return "trainer/edit-class";
    }

    /**
     * Обработка обновления существующего фитнес-класса
     */
    @PostMapping("/edit/{id}")
    public String updateFitnessClass(@PathVariable Long id,
                                     @Valid @ModelAttribute("fitnessClass") FitnessClass fitnessClass,
                                     BindingResult bindingResult,
                                     Authentication authentication,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            return "trainer/edit-class"; // Возвращает форму с текущими данными и ошибками
        }

        Optional<FitnessClass> existingOpt = fitnessClassService.getFitnessClassById(id);
        if (existingOpt.isEmpty()) {
            return "redirect:/error/404";
        }

        FitnessClass existingClass = existingOpt.get();

        // Проверка, что текущий пользователь является владельцем класса
        String username = authentication.getName();
        if (!existingClass.getTrainer().getUser().getUsername().equals(username)) {
            return "redirect:/error/403";
        }

        // Обновление полей
        existingClass.setName(fitnessClass.getName());
        existingClass.setDescription(fitnessClass.getDescription());
        existingClass.setCapacity(fitnessClass.getCapacity());

        fitnessClassService.saveFitnessClass(existingClass);

        return "redirect:/web/trainer/classes";
    }

    /**
     * Обработка удаления существующего фитнес-класса
     */
    @PostMapping("/delete/{id}")
    public String deleteFitnessClass(@PathVariable Long id,
                                     Authentication authentication,
                                     Model model) {
        Optional<FitnessClass> fitnessClassOpt = fitnessClassService.getFitnessClassById(id);

        if (fitnessClassOpt.isEmpty()) {
            return "redirect:/error/404";
        }

        FitnessClass fitnessClass = fitnessClassOpt.get();

        // Проверка, что текущий пользователь является владельцем класса
        String username = authentication.getName();
        if (!fitnessClass.getTrainer().getUser().getUsername().equals(username)) {
            return "redirect:/error/403";
        }

        fitnessClassService.deleteFitnessClass(id);

        return "redirect:/web/trainer/classes";
    }
}
