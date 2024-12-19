package com.fitness.controller.web;

import com.fitness.entity.Schedule;
import com.fitness.entity.FitnessClass;
import com.fitness.entity.Trainer;
import com.fitness.service.*;
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
    private final ClientService clientService;
    private final UserService userService;
    private final TrainerService trainerService; // Добавление поля TrainerService

    @Autowired
    public TrainerScheduleController(ScheduleService scheduleService,
                                     FitnessClassService fitnessClassService,
                                     ClientService clientService,
                                     UserService userService,
                                     TrainerService trainerService) { // Добавление TrainerService в конструктор
        this.scheduleService = scheduleService;
        this.fitnessClassService = fitnessClassService;
        this.clientService = clientService;
        this.userService = userService;
        this.trainerService = trainerService;
    }

    private Trainer getTrainerByUsername(String username) {
        return trainerService.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Тренер не найден с именем пользователя: " + username));
    }


    /**
     * Просмотр расписаний тренера
     */
    @GetMapping
    public String viewSchedules(Model model, Authentication authentication) {
        String username = authentication.getName();
        Trainer trainer = getTrainerByUsername(username); // Получение Trainer

        List<FitnessClass> fitnessClasses = fitnessClassService.findByTrainer(trainer); // Получение всех фитнес-классов тренера

        if (fitnessClasses.isEmpty()) {
            model.addAttribute("errorMessage", "Фитнес-классы тренера не найдены");
            return "trainer/schedules";
        }

        List<Schedule> schedules = scheduleService.findByFitnessClasses(fitnessClasses);
        model.addAttribute("schedules", schedules);
        return "trainer/schedules";
    }


    /**
     * Форма создания нового расписания
     */
    @GetMapping("/create")
    public String createScheduleForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        Trainer trainer = getTrainerByUsername(username); // Получение Trainer

        List<FitnessClass> fitnessClasses = fitnessClassService.findByTrainer(trainer); // Получение всех фитнес-классов тренера

        if (fitnessClasses.isEmpty()) {
            model.addAttribute("errorMessage", "Фитнес-классы тренера не найдены");
            return "trainer/schedules"; // Перенаправление на страницу расписаний с сообщением об ошибке
        }

        model.addAttribute("schedule", new Schedule());
        model.addAttribute("fitnessClasses", fitnessClasses); // Передача списка фитнес-классов
        return "trainer/create-schedule";
    }


    /**
     * Обработка создания нового расписания
     */
    @PostMapping("/create")
    public String createSchedule(@Valid @ModelAttribute("schedule") Schedule schedule,
                                 BindingResult bindingResult,
                                 Authentication authentication,
                                 Model model) {
        String username = authentication.getName();
        Trainer trainer = getTrainerByUsername(username); // Получение Trainer

        List<FitnessClass> fitnessClasses = fitnessClassService.findByTrainer(trainer); // Получение всех фитнес-классов тренера

        if (fitnessClasses.isEmpty()) {
            model.addAttribute("errorMessage", "Фитнес-классы тренера не найдены");
            return "trainer/schedules"; // Перенаправление на страницу расписаний с сообщением об ошибке
        }

        // Валидация: Проверка, что время окончания не раньше начала
        if (schedule.getEndTime().isBefore(schedule.getStartTime())) {
            bindingResult.rejectValue("endTime", "error.schedule", "Время окончания не может быть раньше времени начала.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("fitnessClasses", fitnessClasses);
            return "trainer/create-schedule";
        }

        // Получение выбранного фитнес-класса из формы
        FitnessClass selectedFitnessClass = fitnessClassService.getFitnessClassById(schedule.getFitnessClass().getId())
                .orElseThrow(() -> new RuntimeException("Фитнес-класс не найден"));

        schedule.setFitnessClass(selectedFitnessClass);
        scheduleService.saveSchedule(schedule);
        return "redirect:/web/trainer/schedules";
    }



    /**
     * Форма редактирования расписания
     */
    @GetMapping("/edit/{id}")
    public String editScheduleForm(@PathVariable Long id,
                                   Model model,
                                   Authentication authentication) {
        String username = authentication.getName();
        Trainer trainer = getTrainerByUsername(username); // Получение Trainer

        List<FitnessClass> fitnessClasses = fitnessClassService.findByTrainer(trainer); // Получение всех фитнес-классов тренера

        if (fitnessClasses.isEmpty()) {
            model.addAttribute("errorMessage", "Фитнес-классы тренера не найдены");
            return "trainer/schedules";
        }

        Optional<Schedule> scheduleOpt = scheduleService.findByIdAndTrainer(id, trainer); // Использование Trainer

        if (scheduleOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Расписание не найдено или у вас нет прав на его редактирование");
            return "trainer/schedules";
        }

        Schedule schedule = scheduleOpt.get();
        model.addAttribute("schedule", schedule);
        model.addAttribute("fitnessClasses", fitnessClasses); // Передача списка фитнес-классов
        return "trainer/edit-schedule";
    }




    /**
     * Обработка редактирования расписания
     */
    @PostMapping("/edit/{id}")
    public String editSchedule(@PathVariable Long id,
                               @Valid @ModelAttribute("schedule") Schedule schedule,
                               BindingResult bindingResult,
                               Authentication authentication,
                               Model model) {
        String username = authentication.getName();
        Trainer trainer = getTrainerByUsername(username); // Получение Trainer

        List<FitnessClass> fitnessClasses = fitnessClassService.findByTrainer(trainer); // Получение всех фитнес-классов тренера

        if (fitnessClasses.isEmpty()) {
            model.addAttribute("errorMessage", "Фитнес-классы тренера не найдены");
            return "trainer/schedules";
        }

        Optional<Schedule> existingScheduleOpt = scheduleService.findByIdAndTrainer(id, trainer); // Использование Trainer

        if (existingScheduleOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Расписание не найдено или у вас нет прав на его редактирование");
            return "trainer/schedules";
        }

        Schedule existingSchedule = existingScheduleOpt.get();

        // Валидация: Проверка, что время окончания не раньше начала
        if (schedule.getEndTime().isBefore(schedule.getStartTime())) {
            bindingResult.rejectValue("endTime", "error.schedule", "Время окончания не может быть раньше времени начала.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("fitnessClasses", fitnessClasses);
            return "trainer/edit-schedule";
        }

        // Обновление полей расписания, включая выбор фитнес-класса
        FitnessClass selectedFitnessClass = fitnessClassService.getFitnessClassById(schedule.getFitnessClass().getId())
                .orElseThrow(() -> new RuntimeException("Фитнес-класс не найден"));

        existingSchedule.setFitnessClass(selectedFitnessClass);
        existingSchedule.setStartTime(schedule.getStartTime());
        existingSchedule.setEndTime(schedule.getEndTime());
        // Обновление других полей по необходимости

        scheduleService.saveSchedule(existingSchedule);
        return "redirect:/web/trainer/schedules";
    }




    /**
     * Обработка удаления расписания
     */
    @GetMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<FitnessClass> trainerFitnessClassOpt = fitnessClassService.findFirstFitnessClassByTrainerUsername(username);

        if (trainerFitnessClassOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Фитнес-классы тренера не найдены");
            return "trainer/schedules";
        }

        FitnessClass fitnessClass = trainerFitnessClassOpt.get();
        Optional<Schedule> scheduleOpt = scheduleService.findByIdAndTrainer(id, fitnessClass.getTrainer());

        if (scheduleOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Расписание не найдено или у вас нет прав на его удаление");
            return "trainer/schedules";
        }

        scheduleService.deleteSchedule(id);
        return "redirect:/web/trainer/schedules";
    }
}
