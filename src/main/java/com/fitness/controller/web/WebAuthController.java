package com.fitness.controller.web;

import com.fitness.entity.User;
import com.fitness.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequestMapping("/web/auth")
public class WebAuthController {

    private final UserService userService;

    public WebAuthController(UserService userService) {
        this.userService = userService;
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
        // Проверяем, нет ли такого пользователя
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("errorMessage", "Username is already taken");
            model.addAttribute("user", user);
            return "auth/register";
        }

        // Допустим, по умолчанию будем давать ROLE_CLIENT
        Set<String> roles = Set.of("ROLE_CLIENT");
        userService.registerNewUser(user, roles);

        // После успешной регистрации перенаправим на страницу логина
        return "redirect:/web/auth/login";
    }
}
