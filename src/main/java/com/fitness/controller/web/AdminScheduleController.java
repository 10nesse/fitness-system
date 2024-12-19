package com.fitness.controller.web;

import com.fitness.entity.FitnessClass;
import com.fitness.entity.Schedule;
import com.fitness.service.FitnessClassService;
import com.fitness.service.ScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web/admin/schedules")
public class AdminScheduleController {

    private final ScheduleService scheduleService;
    private final FitnessClassService fitnessClassService;

    public AdminScheduleController(ScheduleService scheduleService, FitnessClassService fitnessClassService) {
        this.scheduleService = scheduleService;
        this.fitnessClassService = fitnessClassService;
    }

    // Список всех расписаний
    @GetMapping
    public String listSchedules(Model model) {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        model.addAttribute("schedules", schedules);
        return "admin/schedules";
    }

    // Форма создания нового расписания
    @GetMapping("/create")
    public String createScheduleForm(Model model) {
        model.addAttribute("schedule", new Schedule());
        model.addAttribute("fitnessClasses", fitnessClassService.getAllFitnessClasses());
        return "admin/create-schedule";
    }

    // Обработка создания расписания
    @PostMapping("/create")
    public String createSchedule(@Valid @ModelAttribute("schedule") Schedule schedule,
                                 BindingResult bindingResult,
                                 @RequestParam("fitnessClassId") Long fitnessClassId,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("fitnessClasses", fitnessClassService.getAllFitnessClasses());
            return "admin/create-schedule";
        }

        Optional<FitnessClass> fcOpt = fitnessClassService.getFitnessClassById(fitnessClassId);
        if (fcOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Fitness class not found");
            model.addAttribute("fitnessClasses", fitnessClassService.getAllFitnessClasses());
            return "admin/create-schedule";
        }

        schedule.setFitnessClass(fcOpt.get());
        scheduleService.saveSchedule(schedule);

        return "redirect:/web/admin/schedules";
    }

    // Форма редактирования расписания
    @GetMapping("/edit/{id}")
    public String editScheduleForm(@PathVariable Long id, Model model) {
        Schedule existingSchedule = scheduleService.getScheduleById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        model.addAttribute("schedule", existingSchedule);
        model.addAttribute("fitnessClasses", fitnessClassService.getAllFitnessClasses());
        return "admin/edit-schedule";
    }

    // Обработка обновления расписания
    @PostMapping("/edit")
    public String updateSchedule(@Valid @ModelAttribute("schedule") Schedule schedule,
                                 BindingResult bindingResult,
                                 @RequestParam("fitnessClassId") Long fitnessClassId,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("fitnessClasses", fitnessClassService.getAllFitnessClasses());
            return "admin/edit-schedule";
        }

        Optional<FitnessClass> fcOpt = fitnessClassService.getFitnessClassById(fitnessClassId);
        if (fcOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Fitness class not found");
            model.addAttribute("fitnessClasses", fitnessClassService.getAllFitnessClasses());
            return "admin/edit-schedule";
        }

        // Проверяем, что расписание действительно существует
        Schedule existingSchedule = scheduleService.getScheduleById(schedule.getId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        existingSchedule.setStartTime(schedule.getStartTime());
        existingSchedule.setEndTime(schedule.getEndTime());
        existingSchedule.setFitnessClass(fcOpt.get());

        scheduleService.saveSchedule(existingSchedule);

        return "redirect:/web/admin/schedules";
    }

    // Удаление расписания
    @GetMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return "redirect:/web/admin/schedules";
    }
}
