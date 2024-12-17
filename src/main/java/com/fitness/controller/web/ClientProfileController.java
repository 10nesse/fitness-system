package com.fitness.controller.web;

import com.fitness.entity.Client;
import com.fitness.entity.User;
import com.fitness.service.ClientService;
import com.fitness.service.UserService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/web/client/profile")
public class ClientProfileController {

    private final ClientService clientService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ClientProfileController(ClientService clientService, UserService userService, PasswordEncoder passwordEncoder) {
        this.clientService = clientService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // Просмотр профиля клиента
    @GetMapping
    public String viewProfile(Model model, Authentication authentication) {
        String username = authentication.getName(); // Получаем username
        Client client = clientService.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        model.addAttribute("client", client);
        return "client/profile";
    }

    // Форма редактирования профиля
    @GetMapping("/edit")
    public String editProfileForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        Client client = clientService.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        model.addAttribute("client", client);
        return "client/edit-profile";
    }

    // Обработка обновления профиля
    @PostMapping("/edit")
    public String updateProfile(
            @Valid @ModelAttribute("client") Client client,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "client/edit-profile";
        }

        String username = authentication.getName();
        Client existingClient = clientService.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Обновление полей
        existingClient.setFirstName(client.getFirstName());
        existingClient.setLastName(client.getLastName());
        existingClient.setPhoneNumber(client.getPhoneNumber());
        // Обновление других полей по необходимости

        clientService.saveClient(existingClient);

        return "redirect:/web/client/profile";
    }

    // Форма изменения пароля
    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("passwordForm", new PasswordForm());
        return "client/change-password";
    }

    // Обработка изменения пароля
    @PostMapping("/change-password")
    public String changePassword(
            @Valid @ModelAttribute("passwordForm") PasswordForm passwordForm,
            BindingResult bindingResult,
            Authentication authentication,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "client/change-password";
        }

        if (!passwordForm.isPasswordsMatching()) {
            bindingResult.rejectValue("confirmPassword", "error.passwordForm", "Пароли не совпадают");
            return "client/change-password";
        }

        String username = authentication.getName();
        Optional<Client> clientOpt = clientService.findByUserUsername(username);
        if (clientOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Client not found");
            return "client/change-password";
        }

        Client client = clientOpt.get();
        User user = client.getUser();

        // Проверка старого пароля
        if (!passwordEncoder.matches(passwordForm.getOldPassword(), user.getPassword())) {
            bindingResult.rejectValue("oldPassword", "error.passwordForm", "Неверный старый пароль");
            return "client/change-password";
        }

        // Установка нового пароля
        user.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
        userService.saveUser(user);

        model.addAttribute("successMessage", "Пароль успешно изменен");
        return "client/change-password";
    }


    // Внутренний класс для формы изменения пароля
    public static class PasswordForm {
        @NotNull(message = "Старый пароль обязателен")
        private String oldPassword;

        @NotNull(message = "Новый пароль обязателен")
        @Size(min = 6, message = "Пароль должен быть минимум 6 символов")
        private String newPassword;

        @NotNull(message = "Подтверждение пароля обязательно")
        @Size(min = 6, message = "Пароль должен быть минимум 6 символов")
        private String confirmPassword;

        // Геттеры и сеттеры

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

        // Дополнительная валидация: проверка совпадения нового пароля и подтверждения
        public boolean isPasswordsMatching() {
            return newPassword != null && newPassword.equals(confirmPassword);
        }
    }
}
