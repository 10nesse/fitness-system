package com.fitness.controller.web;

import com.fitness.entity.Subscription;
import com.fitness.entity.Client;
import com.fitness.service.SubscriptionService;
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
@RequestMapping("/web/client/subscriptions")
public class ClientSubscriptionController {

    private static final Logger log = LoggerFactory.getLogger(ClientSubscriptionController.class);

    private final SubscriptionService subscriptionService;
    private final ClientService clientService;

    @Autowired
    public ClientSubscriptionController(SubscriptionService subscriptionService,
                                        ClientService clientService) {
        this.subscriptionService = subscriptionService;
        this.clientService = clientService;
    }

    @GetMapping
    public String viewSubscriptions(Model model, Authentication authentication) {
        String username = authentication.getName(); // Получаем username
        log.debug("Просмотр подписок для пользователя: {}", username);
        Optional<Client> clientOpt = clientService.findByUserUsername(username);

        if (clientOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Клиент не найден");
            log.warn("Клиент с username '{}' не найден", username);
            return "client/subscriptions"; // Шаблон с сообщением об ошибке
        }

        Client client = clientOpt.get();
        log.debug("Найден клиент: {}", client);
        List<Subscription> subscriptions = subscriptionService.findByClientId(client.getId());
        log.debug("Найдено подписок: {}", subscriptions.size());

        model.addAttribute("subscriptions", subscriptions);
        return "client/subscriptions"; // Шаблон для отображения подписок
    }
}
