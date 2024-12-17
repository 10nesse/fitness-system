package com.fitness.controller.web;

import com.fitness.entity.Client;
import com.fitness.service.ClientService;
import com.fitness.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/web/client/profile")
public class ClientProfileController {

    private final ClientService clientService;
    private final UserService userService;

    @Autowired
    public ClientProfileController(ClientService clientService, UserService userService) {
        this.clientService = clientService;
        this.userService = userService;
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
}
