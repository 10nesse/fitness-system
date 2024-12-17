// src/main/java/com/fitness/controller/web/WebPaymentController.java
package com.fitness.controller.web;

import com.fitness.entity.Payment;
import com.fitness.entity.Subscription;
import com.fitness.service.PaymentService;
import com.fitness.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web/admin/payments")
public class WebPaymentController {

    private final PaymentService paymentService;
    private final SubscriptionService subscriptionService;

    @Autowired
    public WebPaymentController(PaymentService paymentService, SubscriptionService subscriptionService) {
        this.paymentService = paymentService;
        this.subscriptionService = subscriptionService;
    }

    // Список всех платежей
    @GetMapping
    public String listPayments(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        return "admin/payments"; // Шаблон для отображения списка платежей
    }

    // Форма создания нового платежа
    @GetMapping("/create")
    public String createPaymentForm(Model model) {
        model.addAttribute("payment", new Payment());
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();
        model.addAttribute("subscriptions", subscriptions); // Список абонементов для выбора
        return "admin/create-payment"; // Шаблон для создания платежа
    }

    // Обработка создания платежа
    @PostMapping("/create")
    public String createPayment(
            @Valid @ModelAttribute("payment") Payment payment,
            BindingResult bindingResult,
            @RequestParam("subscriptionId") Long subscriptionId,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("subscriptions", subscriptionService.getAllSubscriptions());
            return "admin/create-payment";
        }

        Optional<Subscription> subscriptionOpt = subscriptionService.getSubscriptionById(subscriptionId);

        if (subscriptionOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Subscription not found");
            model.addAttribute("subscriptions", subscriptionService.getAllSubscriptions());
            return "admin/create-payment";
        }

        // Устанавливаем абонемент для платежа
        payment.setSubscription(subscriptionOpt.get());

        // Сохраняем платеж
        paymentService.savePayment(payment);

        return "redirect:/web/admin/payments";
    }

    // Форма редактирования платежа
    @GetMapping("/edit/{id}")
    public String editPaymentForm(@PathVariable Long id, Model model) {
        Payment existingPayment = paymentService.getPaymentById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        model.addAttribute("payment", existingPayment);
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();
        model.addAttribute("subscriptions", subscriptions);
        return "admin/edit-payment"; // Шаблон для редактирования платежа
    }

    // Обработка обновления платежа
    @PostMapping("/edit")
    public String updatePayment(
            @Valid @ModelAttribute("payment") Payment payment,
            BindingResult bindingResult,
            @RequestParam("subscriptionId") Long subscriptionId,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("subscriptions", subscriptionService.getAllSubscriptions());
            return "admin/edit-payment";
        }

        Optional<Subscription> subscriptionOpt = subscriptionService.getSubscriptionById(subscriptionId);

        if (subscriptionOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Subscription not found");
            model.addAttribute("subscriptions", subscriptionService.getAllSubscriptions());
            return "admin/edit-payment";
        }

        // Загружаем существующий платеж
        Payment existingPayment = paymentService.getPaymentById(payment.getId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Обновляем поля
        existingPayment.setAmount(payment.getAmount());
        existingPayment.setPaymentDate(payment.getPaymentDate());
        existingPayment.setSubscription(subscriptionOpt.get());

        // Сохраняем обновленный платеж
        paymentService.savePayment(existingPayment);

        return "redirect:/web/admin/payments";
    }

    // Удаление платежа
    @GetMapping("/delete/{id}")
    public String deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return "redirect:/web/admin/payments";
    }
}
