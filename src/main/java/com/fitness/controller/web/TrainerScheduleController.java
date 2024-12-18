package com.fitness.controller.web;

import com.fitness.entity.FitnessClass;
import com.fitness.entity.Schedule;
import com.fitness.entity.Trainer;
import com.fitness.entity.User;
import com.fitness.service.FitnessClassService;
import com.fitness.service.ScheduleService;
import com.fitness.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web/trainer/schedules")
public class TrainerScheduleController {

    private final ScheduleService scheduleService;
    private final FitnessClassService fitnessClassService;
    private final UserService userService;

    @Autowired
    public TrainerScheduleController(ScheduleService scheduleService,
                                     FitnessClassService fitnessClassService,
                                     UserService userService) {
        this.scheduleService = scheduleService;
        this.fitnessClassService = fitnessClassService;
        this.userService = userService;
    }

    // Просмотр расписаний тренера
    @GetMapping
    public String listSchedules(Model model, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Пользователь не найден");
            return "trainer/schedules";
        }

        User user = userOpt.get();
        Optional<Trainer> trainerOpt = user.getTrainer();

        if (trainerOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Тренер не найден");
            return "trainer/schedules";
        }

        Trainer trainer = trainerOpt.get();
        List<FitnessClass> fitnessClasses = fitnessClassService.findByTrainer(trainer);
        List<Schedule> schedules = scheduleService.findByFitnessClasses(fitnessClasses);

        model.addAttribute("schedules", schedules);
        model.addAttribute("fitnessClasses", fitnessClasses); // Для создания/редактирования расписаний
        return "trainer/schedules";
    }

    // Переход к форме создания расписания
    @GetMapping("/create")
    public String showCreateForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Пользователь не найден");
            return "trainer/create-schedule";
        }

        User user = userOpt.get();
        Optional<Trainer> trainerOpt = user.getTrainer();

        if (trainerOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Тренер не найден");
            return "trainer/create-schedule";
        }

        Trainer trainer = trainerOpt.get();
        List<FitnessClass> fitnessClasses = fitnessClassService.findByTrainer(trainer);

        model.addAttribute("schedule", new Schedule());
        model.addAttribute("fitnessClasses", fitnessClasses);
        return "trainer/create-schedule";
    }

    // Обработка создания расписания
    @PostMapping("/create")
    public String createSchedule(@Valid @ModelAttribute("schedule") Schedule schedule,
                                 BindingResult bindingResult,
                                 @RequestParam("fitnessClassId") Long fitnessClassId,
                                 Model model,
                                 Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Пользователь не найден");
            return "trainer/create-schedule";
        }

        User user = userOpt.get();
        Optional<Trainer> trainerOpt = user.getTrainer();

        if (trainerOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Тренер не найден");
            return "trainer/create-schedule";
        }

        Trainer trainer = trainerOpt.get();
        Optional<FitnessClass> fitnessClassOpt = fitnessClassService.findByIdAndTrainer(fitnessClassId, trainer);

        if (fitnessClassOpt.isEmpty()) {
            bindingResult.rejectValue("fitnessClass", "error.schedule", "Фитнес-класс не найден или не принадлежит вам");
        }

        if (bindingResult.hasErrors()) {
            List<FitnessClass> fitnessClasses = fitnessClassService.findByTrainer(trainer);
            model.addAttribute("fitnessClasses", fitnessClasses);
            return "trainer/create-schedule";
        }

        schedule.setFitnessClass(fitnessClassOpt.get());
        scheduleService.saveSchedule(schedule);
        return "redirect:/web/trainer/schedules";
    }

    // Переход к форме редактирования расписания
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<Schedule> scheduleOpt = scheduleService.findById(id);

        if (scheduleOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Расписание не найдено");
            return "trainer/edit-schedule";
        }

        Schedule schedule = scheduleOpt.get();

        // Проверка принадлежности расписания тренеру
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Пользователь не найден");
            return "trainer/edit-schedule";
        }

        User user = userOpt.get();
        Optional<Trainer> trainerOpt = user.getTrainer();

        if (trainerOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Тренер не найден");
            return "trainer/edit-schedule";
        }

        Trainer trainer = trainerOpt.get();
        Optional<FitnessClass> fitnessClassOpt = fitnessClassService.findByIdAndTrainer(schedule.getFitnessClass().getId(), trainer);

        if (fitnessClassOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Вы не имеете права редактировать это расписание");
            return "trainer/edit-schedule";
        }

        List<FitnessClass> fitnessClasses = fitnessClassService.findByTrainer(trainer);
        model.addAttribute("schedule", schedule);
        model.addAttribute("fitnessClasses", fitnessClasses);
        return "trainer/edit-schedule";
    }

    // Обработка редактирования расписания
    @PostMapping("/edit")
    public String editSchedule(@Valid @ModelAttribute("schedule") Schedule schedule,
                               BindingResult bindingResult,
                               @RequestParam("fitnessClassId") Long fitnessClassId,
                               Model model,
                               Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Пользователь не найден");
            return "trainer/edit-schedule";
        }

        User user = userOpt.get();
        Optional<Trainer> trainerOpt = user.getTrainer();

        if (trainerOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Тренер не найден");
            return "trainer/edit-schedule";
        }

        Trainer trainer = trainerOpt.get();
        Optional<FitnessClass> fitnessClassOpt = fitnessClassService.findByIdAndTrainer(fitnessClassId, trainer);

        if (fitnessClassOpt.isEmpty()) {
            bindingResult.rejectValue("fitnessClass", "error.schedule", "Фитнес-класс не найден или не принадлежит вам");
        }

        if (bindingResult.hasErrors()) {
            List<FitnessClass> fitnessClasses = fitnessClassService.findByTrainer(trainer);
            model.addAttribute("fitnessClasses", fitnessClasses);
            return "trainer/edit-schedule";
        }

        schedule.setFitnessClass(fitnessClassOpt.get());
        scheduleService.saveSchedule(schedule);
        return "redirect:/web/trainer/schedules";
    }

    // Обработка удаления расписания
    @GetMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, Authentication authentication, Model model) {
        Optional<Schedule> scheduleOpt = scheduleService.findById(id);

        if (scheduleOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Расписание не найдено");
            return "redirect:/web/trainer/schedules";
        }

        Schedule schedule = scheduleOpt.get();

        // Проверка принадлежности расписания тренеру
        String username = authentication.getName();
        Optional<User> userOpt = userService.findByUsername(username);

        if (userOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Пользователь не найден");
            return "redirect:/web/trainer/schedules";
        }

        User user = userOpt.get();
        Optional<Trainer> trainerOpt = user.getTrainer();

        if (trainerOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Тренер не найден");
            return "redirect:/web/trainer/schedules";
        }

        Trainer trainer = trainerOpt.get();
        Optional<FitnessClass> fitnessClassOpt = fitnessClassService.findByIdAndTrainer(schedule.getFitnessClass().getId(), trainer);

        if (fitnessClassOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Вы не имеете права удалять это расписание");
            return "redirect:/web/trainer/schedules";
        }

        scheduleService.deleteById(id);
        return "redirect:/web/trainer/schedules";
    }
}
