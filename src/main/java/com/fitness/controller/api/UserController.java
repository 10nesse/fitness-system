// src/main/java/com/fitness/controller/UserController.java
package com.fitness.controller.api;

import com.fitness.entity.Role;
import com.fitness.entity.User;
import com.fitness.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Метод для получения всех пользователей
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Метод для создания нового пользователя
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Получаем имена ролей из переданных объектов Role
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        // Регистрируем нового пользователя с указанными ролями
        User createdUser = userService.registerNewUser(user, roleNames);
        return ResponseEntity.status(201).body(createdUser);
    }

    // Другие методы (если есть)
}
