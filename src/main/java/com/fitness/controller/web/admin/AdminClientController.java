package com.fitness.controller.web.admin;

import com.fitness.entity.Client;
import com.fitness.entity.User;
import com.fitness.service.ClientService;
import com.fitness.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web/admin/clients")
public class AdminClientController {

    private final ClientService clientService;
    private final UserService userService;

    public AdminClientController(ClientService clientService, UserService userService) {
        this.clientService = clientService;
        this.userService = userService;
    }

    @GetMapping
    public String listClients(Model model) {
        List<User> clients = userService.getAllByRole("ROLE_CLIENT");
        model.addAttribute("clients", clients);
        return "admin/clients";
    }

    @GetMapping("/create")
    public String createClientForm(Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("users", userService.getAllUsers()); // Список пользователей для выбора
        return "admin/create-client";
    }

    @PostMapping("/create")
    public String createClient(
            @Valid @ModelAttribute("client") Client client,
            BindingResult bindingResult,
            @RequestParam("userId") Long userId,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.getAllUsers());
            return "admin/create-client";
        }

        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            model.addAttribute("errorMessage", "User not found");
            model.addAttribute("users", userService.getAllUsers());
            return "admin/create-client";
        }

        User user = userOpt.get();
        client.setUser(user);
        client.setEmail(user.getEmail()); // Устанавливаем email из User

        clientService.createClient(client);
        return "redirect:/web/admin/clients";
    }

    @GetMapping("/edit/{id}")
    public String editClientForm(@PathVariable Long id, Model model) {
        Client existingClient = clientService.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        model.addAttribute("client", existingClient);
        model.addAttribute("users", userService.getAllUsers());
        return "admin/edit-client";
    }

    @PostMapping("/edit")
    public String updateClient(
            @Valid @ModelAttribute("client") Client client,
            BindingResult bindingResult,
            @RequestParam("userId") Long userId,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.getAllUsers());
            return "admin/edit-client";
        }

        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            model.addAttribute("errorMessage", "User not found");
            model.addAttribute("users", userService.getAllUsers());
            return "admin/edit-client";
        }

        User user = userOpt.get();
        client.setUser(user);
        client.setEmail(user.getEmail()); // Устанавливаем email из User

        clientService.updateClient(client);

        return "redirect:/web/admin/clients";
    }

    @GetMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id) {
        clientService.deleteById(id);
        return "redirect:/web/admin/clients";
    }
}
