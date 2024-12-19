package com.fitness.controller.web;

import com.fitness.entity.Trainer;
import com.fitness.entity.User;
import com.fitness.service.TrainerService;
import com.fitness.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web/admin/trainers")
public class AdminTrainerController {

    private final TrainerService trainerService;
    private final UserService userService;

    public AdminTrainerController(TrainerService trainerService, UserService userService) {
        this.trainerService = trainerService;
        this.userService = userService;
    }

    @GetMapping
    public String listTrainers(Model model) {
        model.addAttribute("trainers", trainerService.getAllTrainers());
        return "admin/trainers"; // страница со списком тренеров
    }

    @GetMapping("/create")
    public String createTrainerForm(Model model) {
        model.addAttribute("trainer", new Trainer());
        model.addAttribute("allUsers", userService.getAllUsers()); // список всех пользователей для выбора
        return "admin/create-trainer";
    }

    @PostMapping("/create")
    public String createTrainer(
            @Valid @ModelAttribute("trainer") Trainer trainer,
            BindingResult bindingResult,
            @RequestParam("userId") Long userId,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allUsers", userService.getAllUsers());
            return "admin/create-trainer";
        }

        // Проверяем, выбран ли пользователь
        Optional<User> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()) {
            model.addAttribute("errorMessage", "User not found");
            model.addAttribute("allUsers", userService.getAllUsers());
            return "admin/create-trainer";
        }

        // Привязываем выбранного пользователя к тренеру
        trainer.setUser(optionalUser.get());

        // Сохраняем тренера
        trainerService.saveTrainer(trainer);

        return "redirect:/web/admin/trainers";
    }

    @GetMapping("/edit/{id}")
    public String editTrainerForm(@PathVariable Long id, Model model) {
        Trainer existingTrainer = trainerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        model.addAttribute("trainer", existingTrainer);
        model.addAttribute("allUsers", userService.getAllUsers());
        return "admin/edit-trainer";
    }

    @PostMapping("/edit")
    public String updateTrainer(
            @Valid @ModelAttribute("trainer") Trainer trainer,
            BindingResult bindingResult,
            @RequestParam("userId") Long userId,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allUsers", userService.getAllUsers());
            return "admin/edit-trainer";
        }

        Optional<User> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()) {
            model.addAttribute("errorMessage", "User not found");
            model.addAttribute("allUsers", userService.getAllUsers());
            return "admin/edit-trainer";
        }

        trainer.setUser(optionalUser.get());
        trainerService.saveTrainer(trainer);

        return "redirect:/web/admin/trainers";
    }

    @GetMapping("/delete/{id}")
    public String deleteTrainer(@PathVariable Long id) {
        trainerService.deleteById(id);
        return "redirect:/web/admin/trainers";
    }
}
