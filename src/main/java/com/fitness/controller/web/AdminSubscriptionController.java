// src/main/java/com/fitness/controller/web/WebSubscriptionController.java
package com.fitness.controller.web;

import com.fitness.entity.Client;
import com.fitness.entity.FitnessClass;
import com.fitness.entity.Subscription;
import com.fitness.service.ClientService;
import com.fitness.service.FitnessClassService;
import com.fitness.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web/admin/subscriptions")
public class AdminSubscriptionController {

    private final SubscriptionService subscriptionService;
    private final ClientService clientService;
    private final FitnessClassService fitnessClassService;

    @Autowired
    public AdminSubscriptionController(SubscriptionService subscriptionService,
                                     ClientService clientService,
                                     FitnessClassService fitnessClassService) {
        this.subscriptionService = subscriptionService;
        this.clientService = clientService;
        this.fitnessClassService = fitnessClassService;
    }

    // Список всех абонементов
    @GetMapping
    public String listSubscriptions(Model model) {
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();
        model.addAttribute("subscriptions", subscriptions);
        return "admin/subscriptions"; // Шаблон для отображения списка
    }

    // Форма создания нового абонемента
    @GetMapping("/create")
    public String createSubscriptionForm(Model model) {
        model.addAttribute("subscription", new Subscription());
        model.addAttribute("clients", clientService.getAllClients()); // Список клиентов для выбора
        model.addAttribute("fitnessClasses", fitnessClassService.getAllFitnessClasses()); // Список фитнес-классов для выбора
        return "admin/create-subscription"; // Шаблон для создания
    }

    // Обработка создания абонемента
    @PostMapping("/create")
    public String createSubscription(
            @Valid @ModelAttribute("subscription") Subscription subscription,
            BindingResult bindingResult,
            @RequestParam("clientId") Long clientId,
            @RequestParam("fitnessClassId") Long fitnessClassId,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("clients", clientService.getAllClients());
            model.addAttribute("fitnessClasses", fitnessClassService.getAllFitnessClasses());
            return "admin/create-subscription";
        }

        Optional<Client> clientOpt = clientService.findById(clientId);
        Optional<FitnessClass> fitnessClassOpt = fitnessClassService.getFitnessClassById(fitnessClassId);

        if (clientOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Client not found");
            model.addAttribute("clients", clientService.getAllClients());
            model.addAttribute("fitnessClasses", fitnessClassService.getAllFitnessClasses());
            return "admin/create-subscription";
        }

        if (fitnessClassOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Fitness Class not found");
            model.addAttribute("clients", clientService.getAllClients());
            model.addAttribute("fitnessClasses", fitnessClassService.getAllFitnessClasses());
            return "admin/create-subscription";
        }

        // Устанавливаем клиента и фитнес-класс
        subscription.setClient(clientOpt.get());
        subscription.setFitnessClass(fitnessClassOpt.get());

        // Сохраняем абонемент
        subscriptionService.saveSubscription(subscription);

        return "redirect:/web/admin/subscriptions";
    }

    // Форма редактирования абонемента
    @GetMapping("/edit/{id}")
    public String editSubscriptionForm(@PathVariable Long id, Model model) {
        Subscription existingSubscription = subscriptionService.getSubscriptionById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        model.addAttribute("subscription", existingSubscription);
        model.addAttribute("clients", clientService.getAllClients());
        model.addAttribute("fitnessClasses", fitnessClassService.getAllFitnessClasses());
        return "admin/edit-subscription"; // Шаблон для редактирования
    }

    // Обработка обновления абонемента
    @PostMapping("/edit")
    public String updateSubscription(
            @Valid @ModelAttribute("subscription") Subscription subscription,
            BindingResult bindingResult,
            @RequestParam("clientId") Long clientId,
            @RequestParam("fitnessClassId") Long fitnessClassId,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("clients", clientService.getAllClients());
            model.addAttribute("fitnessClasses", fitnessClassService.getAllFitnessClasses());
            return "admin/edit-subscription";
        }

        Optional<Client> clientOpt = clientService.findById(clientId);
        Optional<FitnessClass> fitnessClassOpt = fitnessClassService.getFitnessClassById(fitnessClassId);

        if (clientOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Client not found");
            model.addAttribute("clients", clientService.getAllClients());
            model.addAttribute("fitnessClasses", fitnessClassService.getAllFitnessClasses());
            return "admin/edit-subscription";
        }

        if (fitnessClassOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Fitness Class not found");
            model.addAttribute("clients", clientService.getAllClients());
            model.addAttribute("fitnessClasses", fitnessClassService.getAllFitnessClasses());
            return "admin/edit-subscription";
        }

        // Загружаем существующий абонемент
        Subscription existingSubscription = subscriptionService.getSubscriptionById(subscription.getId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        // Обновляем поля
        existingSubscription.setStartDate(subscription.getStartDate());
        existingSubscription.setEndDate(subscription.getEndDate());
        existingSubscription.setPrice(subscription.getPrice());
        existingSubscription.setClient(clientOpt.get());
        existingSubscription.setFitnessClass(fitnessClassOpt.get());

        // Сохраняем обновлённый абонемент
        subscriptionService.saveSubscription(existingSubscription);

        return "redirect:/web/admin/subscriptions";
    }

    // Удаление абонемента
    @GetMapping("/delete/{id}")
    public String deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return "redirect:/web/admin/subscriptions";
    }
}
