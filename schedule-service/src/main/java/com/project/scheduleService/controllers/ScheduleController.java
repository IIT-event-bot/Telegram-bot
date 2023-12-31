package com.project.scheduleService.controllers;

import com.project.scheduleService.models.WeekType;
import com.project.scheduleService.models.dto.ScheduleDto;
import com.project.scheduleService.service.schedule.AcademicYearService;
import com.project.scheduleService.service.schedule.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService service;
    private final AcademicYearService academicYearService;

    @Autowired
    public ScheduleController(ScheduleService service, AcademicYearService academicYearService) {
        this.service = service;
        this.academicYearService = academicYearService;
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupSchedule(@PathVariable("groupId") long groupId) {
        return ResponseEntity.ok(service.getGroupSchedule(groupId));
    }

    @GetMapping("/{groupId}/{weekType}")
    public ResponseEntity<?> getWeek(@PathVariable("groupId") long groupId,
                                     @PathVariable("weekType") WeekType weekTitle) {
        return ResponseEntity.ok(service.getWeek(groupId, weekTitle));
    }

    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleDto schedule) {
        service.createSchedule(schedule);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<?> updateSchedule(@RequestBody ScheduleDto schedule) {
        service.updateSchedule(schedule);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable("groupId") long groupId) {
        service.deleteSchedule(groupId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/academicYear")
    public ResponseEntity<?> setStartAcademicYear(@RequestParam LocalDate date,
                                                  @RequestParam WeekType weekType) {
        academicYearService.setStartAcademicYear(date, weekType);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/academicYear")
    public ResponseEntity<?> updateAcademicYear(@RequestParam LocalDate date,
                                                @RequestParam WeekType weekType) {
        academicYearService.updateAcademicYear(date, weekType);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/academicYear")
    public ResponseEntity<?> getAcademicYear() {
        return ResponseEntity.ok(academicYearService.getAcademicYear());
    }
}
