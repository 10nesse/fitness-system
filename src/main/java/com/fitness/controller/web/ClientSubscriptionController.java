// src/main/java/com/fitness/controller/web/ClientSubscriptionController.java
package com.fitness.controller.web;

import com.fitness.entity.Subscription;
import com.fitness.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/web/client/subscriptions")
public class ClientSubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public ClientSubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // Просмотр абонементов клиента
    @GetMapping
    public String viewSubscriptions(Model model, Authentication authentication) {
        String email = authentication.getName();
        List<Subscription> subscriptions = subscriptionService.findByClientEmail(email);
        model.addAttribute("subscriptions", subscriptions);
        return "client/subscriptions";
    }
}
