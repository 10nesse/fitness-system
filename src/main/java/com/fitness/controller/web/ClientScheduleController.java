package com.fitness.controller.web;

import com.fitness.entity.Schedule;
import com.fitness.entity.Subscription;
import com.fitness.entity.Client;
import com.fitness.service.ScheduleService;
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

@Controller
@RequestMapping("/web/client/schedules")
public class ClientScheduleController {

    private final ScheduleService scheduleService;
    private final SubscriptionService subscriptionService;
    private final ClientService clientService;

    @Autowired
    public ClientScheduleController(ScheduleService scheduleService,
                                    SubscriptionService subscriptionService,
                                    ClientService clientService) {
        this.scheduleService = scheduleService;
        this.subscriptionService = subscriptionService;
        this.clientService = clientService;
    }

    @GetMapping
    public String viewSchedules(Model model, Authentication authentication) {
        String username = authentication.getName(); // Получаем username
        Optional<Client> clientOpt = clientService.findByUserUsername(username);

        if (clientOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Клиент не найден");
            return "client/schedules"; // Шаблон с сообщением об ошибке
        }

        Client client = clientOpt.get();
        List<Subscription> subscriptions = subscriptionService.findByClientId(client.getId());
        List<Schedule> schedules = scheduleService.findSchedulesBySubscriptions(subscriptions);

        model.addAttribute("schedules", schedules);
        return "client/schedules"; // Шаблон для отображения расписаний
    }
}
