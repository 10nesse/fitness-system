package com.fitness.controller.web.trainer;

import com.fitness.entity.Trainer;
import com.fitness.entity.User;
import com.fitness.service.TrainerService;
import com.fitness.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Controller
@RequestMapping("/web/trainer/profile")
public class TrainerProfileController {
    private final TrainerService trainerService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TrainerProfileController(TrainerService trainerService, UserService userService, PasswordEncoder passwordEncoder) {
        this.trainerService = trainerService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Просмотр профиля тренера.
     */
    @GetMapping
    public String viewProfile(Model model, Authentication authentication) {
        String username = authentication.getName(); // Получаем текущего пользователя
        Trainer trainer = trainerService.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Тренер не найден"));
        model.addAttribute("trainer", trainer);
        return "trainer/profile";
    }

    /**
     * Форма редактирования профиля тренера.
     */
    @GetMapping("/edit")
    public String editProfileForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        Trainer trainer = trainerService.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Тренер не найден"));
        model.addAttribute("trainer", trainer);
        return "trainer/edit-profile";
    }

    /**
     * Обработка обновления профиля тренера.
     */
    @PostMapping("/edit")
    public String updateProfile(
            @Valid @ModelAttribute("trainer") Trainer trainer,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "trainer/edit-profile";
        }

        String username = authentication.getName();
        Trainer existingTrainer = trainerService.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Тренер не найден"));

        // Обновление данных профиля
        existingTrainer.setFirstName(trainer.getFirstName());
        existingTrainer.setLastName(trainer.getLastName());
        existingTrainer.setSpecialization(trainer.getSpecialization());
        existingTrainer.setPhoneNumber(trainer.getPhoneNumber());
        trainerService.saveTrainer(existingTrainer);

        return "redirect:/web/trainer/profile";
    }

    /**
     * Форма изменения пароля.
     */
    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("passwordForm", new PasswordForm());
        return "trainer/change-password";
    }

    /**
     * Обработка изменения пароля.
     */
    @PostMapping("/change-password")
    public String changePassword(
            @Valid @ModelAttribute("passwordForm") PasswordForm passwordForm,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "trainer/change-password";
        }

        if (!passwordForm.isPasswordsMatching()) {
            bindingResult.rejectValue("confirmPassword", "error.passwordForm", "Пароли не совпадают");
            return "trainer/change-password";
        }

        String username = authentication.getName();
        Trainer trainer = trainerService.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Тренер не найден"));

        User user = trainer.getUser();

        // Проверка старого пароля
        if (!passwordEncoder.matches(passwordForm.getOldPassword(), user.getPassword())) {
            bindingResult.rejectValue("oldPassword", "error.passwordForm", "Неверный старый пароль");
            return "trainer/change-password";
        }

        // Установка нового пароля
        user.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
        userService.saveUser(user);

        model.addAttribute("successMessage", "Пароль успешно изменен");
        return "trainer/change-password";
    }

    /**
     * Класс для формы изменения пароля.
     */
    public static class PasswordForm {
        @NotNull(message = "Старый пароль обязателен")
        private String oldPassword;

        @NotNull(message = "Новый пароль обязателен")
        @Size(min = 6, message = "Пароль должен быть минимум 6 символов")
        private String newPassword;

        @NotNull(message = "Подтверждение пароля обязательно")
        @Size(min = 6, message = "Пароль должен быть минимум 6 символов")
        private String confirmPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }

        public boolean isPasswordsMatching() {
            return newPassword != null && newPassword.equals(confirmPassword);
        }
    }
}
