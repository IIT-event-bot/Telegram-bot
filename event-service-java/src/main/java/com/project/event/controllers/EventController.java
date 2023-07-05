package com.project.event.controllers;

import com.project.event.models.EventDto;
import com.project.event.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/event")
public class EventController {
    private final EventService service;

    @Autowired
    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAllEvents() {
        return ResponseEntity.ok(service.getAllEvents());
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable("eventId") long eventId) {
        return ResponseEntity.ok(service.getEventById(eventId));
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody EventDto event) {
        service.createEvent(event);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") long eventId,
                                         @RequestBody EventDto event) {
        event.setId(eventId);
        service.updateEvent(event);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable("eventId") long eventId) {
        service.deleteEventById(eventId);
        return ResponseEntity.ok().build();
    }
}
