package com.fitness.controller.web;

import com.fitness.entity.Client;
import com.fitness.entity.User;
import com.fitness.service.ClientService;
import com.fitness.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/web/admin/clients")
public class WebClientController {

    private final ClientService clientService;
    private final UserService userService;

    public WebClientController(ClientService clientService, UserService userService) {
        this.clientService = clientService;
        this.userService = userService;
    }

    @GetMapping
    public String listClients(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        return "admin/clients"; // Страница списка клиентов
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

        client.setUser(userOpt.get());
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

        client.setUser(userOpt.get());
        clientService.updateClient(client);

        return "redirect:/web/admin/clients";
    }

    @GetMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id) {
        clientService.deleteById(id);
        return "redirect:/web/admin/clients";
    }
}
