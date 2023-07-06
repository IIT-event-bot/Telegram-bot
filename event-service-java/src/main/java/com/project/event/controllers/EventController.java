package com.project.event.controllers;

import com.project.event.models.Event;
import com.project.event.services.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/event")
@Tag(name = "Event service")
public class EventController {
    private final EventService service;

    @Autowired
    public EventController(EventService service) {
        this.service = service;
    }

    @Operation(summary = "Получение всех событий")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = Event.class
                                            )
                                    )
                            )
                    }
            )
    })
    @GetMapping
    public ResponseEntity<?> getAllEvents() {
        return ResponseEntity.ok(service.getAllEvents());
    }


    @Operation(summary = "Получение события по id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Event.class
                                    )
                            )
                    }
            )
    })
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable("eventId") long eventId) {
        return ResponseEntity.ok(service.getEventById(eventId));
    }

    @Operation(summary = "Создание события",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(
                                    implementation = Event.class
                            )
                    )
            ))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201"
            )
    })
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        service.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Обновление события по id",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(
                                    implementation = Event.class
                            )
                    )
            ))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            )
    })
    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") long eventId,
                                         @RequestBody Event event) {
        event.setId(eventId);
        service.updateEvent(event);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удаление события")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            )
    })
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable("eventId") long eventId) {
        service.deleteEventById(eventId);
        return ResponseEntity.ok().build();
    }
}
