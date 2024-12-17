package com.fitness.exception;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalWebExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public String handleWebRuntimeException(RuntimeException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error-page"; // имя шаблона ошибки для Thymeleaf
    }

}
