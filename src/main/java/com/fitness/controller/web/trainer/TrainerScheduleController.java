package com.fitness.controller.web.trainer;

import com.fitness.dto.ScheduleWithClientsDTO;
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

import java.time.format.DateTimeFormatter;
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

    /**
     * Вспомогательный метод для получения Trainer по username
     */
    private Trainer getTrainerByUsername(String username) {
        return trainerService.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Тренер не найден с именем пользователя: " + username));
    }


    /**
     * Просмотр расписаний тренера
     */
    /**
     * Просмотр расписаний тренера с клиентами
     */
    @GetMapping
    public String viewSchedules(Model model, Authentication authentication) {
        String username = authentication.getName();
        Trainer trainer = getTrainerByUsername(username); // Получение Trainer

        List<ScheduleWithClientsDTO> scheduleWithClients = scheduleService.getAllSchedulesWithClients(trainer);

        // Логирование для проверки данных
        scheduleWithClients.forEach(dto -> System.out.println("Schedule ID: " + dto.getSchedule().getId() +
                ", Registered: " + dto.getRegistered() + ", Capacity: " + dto.getCapacity()));

        model.addAttribute("schedules", scheduleWithClients);
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
        Optional<FitnessClass> selectedFitnessClassOpt = fitnessClassService.getFitnessClassById(schedule.getFitnessClass().getId());
        if (selectedFitnessClassOpt.isEmpty()) {
            bindingResult.rejectValue("fitnessClass", "error.schedule", "Выбранный фитнес-класс не найден.");
            model.addAttribute("fitnessClasses", fitnessClasses);
            return "trainer/create-schedule";
        }

        FitnessClass selectedFitnessClass = selectedFitnessClassOpt.get();
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
        Trainer trainer = getTrainerByUsername(username);

        List<FitnessClass> fitnessClasses = fitnessClassService.findByTrainer(trainer);

        if (fitnessClasses.isEmpty()) {
            model.addAttribute("errorMessage", "Фитнес-классы тренера не найдены");
            return "trainer/schedules";
        }

        Schedule existingSchedule = scheduleService.findByIdAndTrainer(id, trainer)
                .orElseThrow(() -> new RuntimeException("Расписание не найдено или у вас нет прав на его редактирование"));

        String formattedStartTime = existingSchedule.getStartTime() != null
                ? existingSchedule.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
                : null;
        String formattedEndTime = existingSchedule.getEndTime() != null
                ? existingSchedule.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
                : null;

        model.addAttribute("schedule", existingSchedule);
        model.addAttribute("formattedStartTime", formattedStartTime);
        model.addAttribute("formattedEndTime", formattedEndTime);
        model.addAttribute("fitnessClasses", fitnessClasses);
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

        Optional<ScheduleWithClientsDTO> existingScheduleOpt = scheduleService.getAllSchedulesWithClients(trainer).stream()
                .filter(dto -> dto.getSchedule().getId().equals(id))
                .findFirst();

        if (existingScheduleOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Расписание не найдено или у вас нет прав на его редактирование");
            return "trainer/schedules";
        }

        ScheduleWithClientsDTO existingScheduleDTO = existingScheduleOpt.get();

        // Валидация: Проверка, что время окончания не раньше начала
        if (schedule.getEndTime().isBefore(schedule.getStartTime())) {
            bindingResult.rejectValue("endTime", "error.schedule", "Время окончания не может быть раньше времени начала.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("fitnessClasses", fitnessClasses);
            return "trainer/edit-schedule";
        }

        // Получение выбранного фитнес-класса из формы
        Optional<FitnessClass> selectedFitnessClassOpt = fitnessClassService.getFitnessClassById(schedule.getFitnessClass().getId());
        if (selectedFitnessClassOpt.isEmpty()) {
            bindingResult.rejectValue("fitnessClass", "error.schedule", "Выбранный фитнес-класс не найден.");
            model.addAttribute("fitnessClasses", fitnessClasses);
            return "trainer/edit-schedule";
        }

        FitnessClass selectedFitnessClass = selectedFitnessClassOpt.get();

        // Обновление полей расписания
        existingScheduleDTO.getSchedule().setFitnessClass(selectedFitnessClass);
        existingScheduleDTO.getSchedule().setStartTime(schedule.getStartTime());
        existingScheduleDTO.getSchedule().setEndTime(schedule.getEndTime());
        // Обновление других полей по необходимости

        scheduleService.saveSchedule(existingScheduleDTO.getSchedule());
        return "redirect:/web/trainer/schedules";
    }




    /**
     * Обработка удаления расписания
     */
    @GetMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, Authentication authentication, Model model) {
        String username = authentication.getName();
        Trainer trainer = getTrainerByUsername(username); // Получение Trainer

        List<FitnessClass> fitnessClasses = fitnessClassService.findByTrainer(trainer); // Получение всех фитнес-классов тренера

        if (fitnessClasses.isEmpty()) {
            model.addAttribute("errorMessage", "Фитнес-классы тренера не найдены");
            return "trainer/schedules";
        }

        Optional<Schedule> scheduleOpt = scheduleService.findByIdAndTrainer(id, trainer);

        if (scheduleOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Расписание не найдено или у вас нет прав на его удаление");
            return "trainer/schedules";
        }

        scheduleService.deleteSchedule(id);
        return "redirect:/web/trainer/schedules";
    }
}
