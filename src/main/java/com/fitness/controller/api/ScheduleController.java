package com.fitness.controller.api;

import com.fitness.entity.Schedule;
import com.fitness.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestBody Schedule schedule) {
        Schedule savedSchedule = scheduleService.saveSchedule(schedule);
        return ResponseEntity.ok(savedSchedule);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Long id) {
        Optional<Schedule> scheduleOpt = scheduleService.getScheduleById(id);
        return scheduleOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Long id, @RequestBody Schedule scheduleDetails) {
        Optional<Schedule> scheduleOpt = scheduleService.getScheduleById(id);
        if (scheduleOpt.isPresent()) {
            Schedule schedule = scheduleOpt.get();
            schedule.setStartTime(scheduleDetails.getStartTime());
            schedule.setEndTime(scheduleDetails.getEndTime());
            schedule.setFitnessClass(scheduleDetails.getFitnessClass());
            Schedule updatedSchedule = scheduleService.saveSchedule(schedule);
            return ResponseEntity.ok(updatedSchedule);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok("Schedule deleted successfully");
    }

    @GetMapping("/fitness-class/{fitnessClassId}")
    public ResponseEntity<List<Schedule>> getSchedulesByFitnessClass(
            @PathVariable Long fitnessClassId
    ) {
        List<Schedule> schedules = scheduleService.findByFitnessClassId(fitnessClassId);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/period")
    public ResponseEntity<List<Schedule>> getSchedulesByPeriod(
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDateTime startDateTime = LocalDateTime.parse(start);
        LocalDateTime endDateTime = LocalDateTime.parse(end);
        List<Schedule> schedules = scheduleService.findByPeriod(startDateTime, endDateTime);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<Schedule>> getSchedulesByTrainerAndPeriod(
            @PathVariable Long trainerId,
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDateTime startDateTime = LocalDateTime.parse(start);
        LocalDateTime endDateTime = LocalDateTime.parse(end);
        List<Schedule> schedules = scheduleService.findByTrainerAndPeriod(trainerId, startDateTime, endDateTime);
        return ResponseEntity.ok(schedules);
    }
}
