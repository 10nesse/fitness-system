package com.fitness.controller.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/admin")
@PreAuthorize("hasRole('ADMIN')")
public class WebAdminController {

    @GetMapping("/settings")
    public String adminSettings() {
        return "admin/settings"; // Шаблон admin/settings.html
    }

    // Другие методы, доступные только ADMIN
}
