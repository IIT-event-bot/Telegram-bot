package com.project.scheduleService.controllers;

import com.project.scheduleService.models.dto.ScheduleDto;
import com.project.scheduleService.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule/{groupId}")
public class ScheduleController {
    private final ScheduleService service;

    @Autowired
    public ScheduleController(ScheduleService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getGroupSchedule(@PathVariable("groupId") long groupId) {
        return ResponseEntity.ok(service.getGroupSchedule(groupId));
    }

    @GetMapping("/{weekTitle}")
    public ResponseEntity<?> getWeek(@PathVariable("groupId") long groupId,
                                     @PathVariable("weekTitle") String weekTitle) {
        return ResponseEntity.ok(service.getWeek(groupId, weekTitle));
    }

    @PostMapping
    public ResponseEntity<?> createSchedule(@PathVariable("groupId") long groupId,
                                            @RequestBody ScheduleDto schedule) {
        service.createSchedule(groupId, schedule);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<?> updateSchedule(@PathVariable("groupId") long groupId,
                                            @RequestBody ScheduleDto schedule) {
        service.updateSchedule(groupId, schedule);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteSchedule(@PathVariable("groupId") long groupId) {
        service.deleteSchedule(groupId);
        return ResponseEntity.ok().build();
    }
}
