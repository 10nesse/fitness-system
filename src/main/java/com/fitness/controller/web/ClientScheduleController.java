package com.fitness.controller.web;

import com.fitness.dto.ScheduleWithCapacityDTO;
import com.fitness.entity.FitnessClass;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/client")
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

    /**
     * Моё расписание - занятия, на которые клиент уже записан
     */
    @GetMapping("/schedules")
    public String viewMySchedules(Model model, Authentication authentication) {
        String username = authentication.getName();
        Client client = clientService.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        List<Schedule> schedules = scheduleService.findSchedulesByClient(client);
        List<ScheduleWithCapacityDTO> schedulesWithCapacity = scheduleService.getSchedulesWithCapacity(schedules);

        model.addAttribute("schedules", schedulesWithCapacity);
        return "client/schedules";
    }

    /**
     * Расписания по абонементам - доступные занятия для клиента
     */
    @GetMapping("/subscription-schedules")
    public String viewSubscriptionSchedules(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Model model,
            Authentication authentication) {

        // Установить даты по умолчанию
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now().plusMonths(1);
        }

        String username = authentication.getName();
        Client client = clientService.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        List<Subscription> subscriptions = subscriptionService.findByClientId(client.getId());
        List<FitnessClass> fitnessClasses = subscriptions.stream()
                .map(Subscription::getFitnessClass)
                .distinct()
                .collect(Collectors.toList());

        List<Schedule> schedules = scheduleService.findSchedulesByFitnessClassesAndPeriod(
                fitnessClasses,
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        List<ScheduleWithCapacityDTO> schedulesWithCapacity = scheduleService.getSchedulesWithCapacity(schedules);

        model.addAttribute("schedules", schedulesWithCapacity);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "client/subscription-schedules";
    }


    /**
     * Регистрация на занятие
     */
    @PostMapping("/subscription-schedules/register/{scheduleId}")
    public String registerToSubscriptionSchedule(
            @PathVariable Long scheduleId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Authentication authentication,
            Model model) {

        String username = authentication.getName();
        Client client = clientService.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));

        Schedule schedule = scheduleService.getScheduleById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Расписание не найдено"));

        try {
            scheduleService.registerClientToSchedule(client, schedule);
            model.addAttribute("successMessage", "Вы успешно записались на занятие.");
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        // Возврат к расписанию с сохранением фильтров
        return viewSubscriptionSchedules(startDate, endDate, model, authentication);
    }

}
