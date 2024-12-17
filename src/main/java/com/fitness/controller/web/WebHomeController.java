package com.fitness.controller.web;

import com.fitness.entity.User;
import com.fitness.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web")
public class WebHomeController {

    private final UserService userService;

    public WebHomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/home")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        model.addAttribute("user", user);
        model.addAttribute("roles", user.getRoles());

        // Получаем имена ролей (например, ROLE_ADMIN, ROLE_TRAINER, ROLE_CLIENT)
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        // Логика перенаправления в зависимости от роли:
        if (roleNames.contains("ROLE_ADMIN")) {
            // Перенаправляем на админский дашборд
            return "admin/admin-dashboard";
        } else if (roleNames.contains("ROLE_TRAINER")) {
            // Перенаправляем на дашборд тренера
            return "trainer/trainer-dashboard";
        } else if (roleNames.contains("ROLE_CLIENT")) {
            // Перенаправляем на дашборд клиента
            return "client/client-dashboard";
        } else {
            // Если никакая специфичная роль не найдена, возвращаем общий дашборд
            return "dashboard";
        }
    }
}
