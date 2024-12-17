// src/main/java/com/fitness/controller/web/ClientPaymentController.java
package com.fitness.controller.web;

import com.fitness.entity.Payment;
import com.fitness.service.PaymentService;
import com.fitness.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/web/client/payments")
public class ClientPaymentController {

    private final PaymentService paymentService;
    private final SubscriptionService subscriptionService;

    @Autowired
    public ClientPaymentController(PaymentService paymentService, SubscriptionService subscriptionService) {
        this.paymentService = paymentService;
        this.subscriptionService = subscriptionService;
    }

    // Просмотр платежей клиента
    @GetMapping
    public String viewPayments(Model model, Authentication authentication) {
        String email = authentication.getName();
        List<Payment> payments = paymentService.findByClientEmail(email);
        model.addAttribute("payments", payments);
        return "client/payments";
    }
}
