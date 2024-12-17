package com.fitness.controller.web;

import com.fitness.entity.Payment;
import com.fitness.entity.Client;
import com.fitness.service.PaymentService;
import com.fitness.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/web/client/payments")
public class ClientPaymentController {

    private static final Logger log = LoggerFactory.getLogger(ClientPaymentController.class);

    private final PaymentService paymentService;
    private final ClientService clientService;

    @Autowired
    public ClientPaymentController(PaymentService paymentService,
                                   ClientService clientService) {
        this.paymentService = paymentService;
        this.clientService = clientService;
    }

    @GetMapping
    public String viewPayments(Model model, Authentication authentication) {
        String username = authentication.getName(); // Получаем username
        log.debug("Просмотр платежей для пользователя: {}", username);
        Optional<Client> clientOpt = clientService.findByUserUsername(username);

        if (clientOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Клиент не найден");
            log.warn("Клиент с username '{}' не найден", username);
            return "client/payments"; // Шаблон с сообщением об ошибке
        }

        Client client = clientOpt.get();
        log.debug("Найден клиент: {}", client);
        List<Payment> payments = paymentService.findByClientId(client.getId());
        log.debug("Найдено платежей: {}", payments.size());

        model.addAttribute("payments", payments);
        return "client/payments"; // Шаблон для отображения платежей
    }
}
