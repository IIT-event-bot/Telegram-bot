package com.project.scheduleService.controllers;

import com.project.scheduleService.models.AcademicYear;
import com.project.scheduleService.models.WeekType;
import com.project.scheduleService.models.dto.ScheduleDto;
import com.project.scheduleService.service.schedule.AcademicYearService;
import com.project.scheduleService.service.schedule.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/schedule")
@Tag(name = "Schedule service")
public class ScheduleController {
    private final ScheduleService service;
    private final AcademicYearService academicYearService;

    @Autowired
    public ScheduleController(ScheduleService service, AcademicYearService academicYearService) {
        this.service = service;
        this.academicYearService = academicYearService;
    }

    @Operation(summary = "Получение расписания группы")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = ScheduleDto.class
                                            )
                                    )
                            )
                    }
            )
    })
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupSchedule(@PathVariable("groupId") long groupId) {
        return ResponseEntity.ok(service.getGroupSchedule(groupId));
    }

    @Operation(summary = "Получение расписания группы на неделю")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = ScheduleDto.class
                                            )
                                    )
                            )
                    }
            )
    })
    @GetMapping("/{groupId}/{weekType}")
    public ResponseEntity<?> getWeek(@PathVariable("groupId") long groupId,
                                     @PathVariable("weekType") WeekType weekTitle) {
        return ResponseEntity.ok(service.getWeek(groupId, weekTitle));
    }

    @Operation(summary = "Создание расписания",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(
                                    implementation = ScheduleDto.class
                            )
                    )
            ))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201"
            )
    })
    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleDto schedule) {
        service.createSchedule(schedule);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Обновление расписания",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(
                                    implementation = ScheduleDto.class
                            )
                    )
            ))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            )
    })
    @PutMapping
    public ResponseEntity<?> updateSchedule(@RequestBody ScheduleDto schedule) {
        service.updateSchedule(schedule);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удаление расписания группы")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            )
    })
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable("groupId") long groupId) {
        service.deleteSchedule(groupId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Создание даты начала семестра")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201"
            )
    })
    @PostMapping("/academicYear")
    public ResponseEntity<?> setStartAcademicYear(@RequestParam LocalDate date,
                                                  @RequestParam WeekType weekType) {
        academicYearService.setStartAcademicYear(date, weekType);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Обновление даты начала семестра")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            )
    })
    @PutMapping("/academicYear")
    public ResponseEntity<?> updateAcademicYear(@RequestParam LocalDate date,
                                                @RequestParam WeekType weekType) {
        academicYearService.updateAcademicYear(date, weekType);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получение даты начала семестра")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = AcademicYear.class
                                            )
                                    )
                            )
                    }
            )
    })
    @GetMapping("/academicYear")
    public ResponseEntity<?> getAcademicYear() {
        return ResponseEntity.ok(academicYearService.getAcademicYear());
    }
}
