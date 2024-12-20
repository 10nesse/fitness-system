package com.fitness.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка исключения ResourceNotFoundException.
     * Возвращает страницу 404 с сообщением об ошибке.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404"; // Шаблон для страницы ошибки 404
    }

    /**
     * Обработка исключения доступа (403 Forbidden).
     * Возвращает страницу 403 с сообщением об ошибке.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        model.addAttribute("errorMessage", "У вас нет доступа к этой странице.");
        return "error/403"; // Шаблон для страницы ошибки 403
    }

    /**
     * Обработка любых других исключений.
     * Возвращает страницу 500 с сообщением об ошибке.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("errorMessage", "Что-то пошло не так. Пожалуйста, попробуйте позже.");
        model.addAttribute("details", ex.getMessage());
        return "error/500"; // Шаблон для страницы ошибки 500
    }
}
