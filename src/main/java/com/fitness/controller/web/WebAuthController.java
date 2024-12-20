package com.fitness.controller.web;

import com.fitness.entity.Client;
import com.fitness.entity.User;
import com.fitness.service.ClientService;
import com.fitness.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequestMapping("/web/auth")
public class WebAuthController {

    private final UserService userService;
    private final ClientService clientService;

    public WebAuthController(UserService userService, ClientService clientService) {
        this.userService = userService;
        this.clientService = clientService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        // Проверяем, есть ли такой пользователь
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("errorMessage", "Имя пользователя уже занято.");
            return "auth/register";
        }

        // Устанавливаем роль клиента
        Set<String> roles = Set.of("ROLE_CLIENT");
        userService.registerNewUser(user, roles);

        // Автоматически добавляем его в таблицу клиентов
        Client client = new Client();
        client.setUser(user);
        client.setFirstName(user.getFirstName());
        client.setLastName(user.getLastName());
        client.setEmail(user.getEmail());
        client.setPhoneNumber(user.getPhoneNumber());
        clientService.createClient(client);

        return "redirect:/web/auth/login";
    }

}
