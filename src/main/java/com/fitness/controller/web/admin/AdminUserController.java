package com.fitness.controller.web.admin;

import com.fitness.entity.Client;
import com.fitness.entity.Trainer;
import com.fitness.entity.User;
import com.fitness.service.ClientService;
import com.fitness.service.TrainerService;
import com.fitness.service.UserService;
import com.fitness.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/web/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final RoleService roleService;
    private final ClientService clientService;
    private final TrainerService trainerService;

    public AdminUserController(UserService userService, RoleService roleService, ClientService clientService, TrainerService trainerService) {
        this.userService = userService;
        this.roleService = roleService;
        this.clientService = clientService;
        this.trainerService = trainerService;
    }

    // Отображение списка пользователей
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    // Форма создания нового пользователя
    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/create-user";
    }

    // Обработка создания нового пользователя
    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("user") User user,
                             @RequestParam("roleNames") Set<String> roleNames,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/create-user";
        }

        // Сохранение пользователя и назначение ролей
        userService.registerNewUser(user, roleNames);

        // Логика для создания записи в таблицах `Client` и `Trainer`
        if (roleNames.contains("ROLE_CLIENT")) {
            Client client = new Client();
            client.setUser(user);
            client.setFirstName(user.getFirstName());
            client.setLastName(user.getLastName());
            client.setEmail(user.getEmail());
            client.setPhoneNumber(user.getPhoneNumber());
            clientService.createClient(client);
        }

        if (roleNames.contains("ROLE_TRAINER")) {
            Trainer trainer = new Trainer();
            trainer.setUser(user);
            trainer.setFirstName(user.getFirstName());
            trainer.setLastName(user.getLastName());
            trainer.setEmail(user.getEmail());
            trainer.setPhoneNumber(user.getPhoneNumber());
            trainerService.createTrainer(trainer);
        }

        return "redirect:/web/admin/users";
    }




    // Форма редактирования пользователя
    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User existingUser = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", existingUser);
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/edit-user";
    }

    @PostMapping("/edit")
    public String updateUser(@Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             @RequestParam("roleNames") Set<String> roleNames,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/edit-user";
        }

        // Получение существующего пользователя
        User existingUser = userService.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Если пароль заполнен, обновляем его
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existingUser.setPassword(userService.encodePassword(user.getPassword()));
        }

        // Обновление остальных данных
        existingUser.setUsername(user.getUsername());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setRoles(roleService.findRolesByNames(roleNames)); // Применение нового метода

        userService.saveUser(existingUser); // Сохранение обновленного пользователя

        return "redirect:/web/admin/users";
    }






    // Обработка удаления пользователя
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/web/admin/users";
    }
}
