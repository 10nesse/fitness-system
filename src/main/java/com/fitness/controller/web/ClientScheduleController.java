// src/main/java/com/fitness/controller/web/ClientScheduleController.java
package com.fitness.controller.web;

import com.fitness.entity.FitnessClass;
import com.fitness.entity.Schedule;
import com.fitness.entity.Subscription;
import com.fitness.service.FitnessClassService;
import com.fitness.service.ScheduleService;
import com.fitness.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/web/client/schedule")
public class ClientScheduleController {

    private final ScheduleService scheduleService;
    private final SubscriptionService subscriptionService;
    private final FitnessClassService fitnessClassService;

    @Autowired
    public ClientScheduleController(ScheduleService scheduleService,
                                    SubscriptionService subscriptionService,
                                    FitnessClassService fitnessClassService) {
        this.scheduleService = scheduleService;
        this.subscriptionService = subscriptionService;
        this.fitnessClassService = fitnessClassService;
    }

    // Просмотр расписания клиента
    @GetMapping
    public String viewSchedule(Model model, Authentication authentication) {
        String email = authentication.getName();
        List<Subscription> subscriptions = subscriptionService.findByClientEmail(email);
        List<Schedule> schedules = scheduleService.findSchedulesBySubscriptions(subscriptions);
        model.addAttribute("schedules", schedules);
        return "client/schedule";
    }
}
