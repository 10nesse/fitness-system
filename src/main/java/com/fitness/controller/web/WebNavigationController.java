package com.fitness.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebNavigationController {

    @GetMapping("/about")
    public String aboutPage() {
        return "about"; // Указываем имя шаблона без папки
    }
}
