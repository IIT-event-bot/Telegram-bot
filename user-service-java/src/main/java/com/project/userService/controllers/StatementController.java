package com.project.userService.controllers;

import com.project.userService.services.StatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statement")
public class StatementController {
    private final StatementService service;

    @Autowired
    public StatementController(StatementService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAllStatements(@RequestParam(value = "filter", required = false) String filter) {
        return ResponseEntity.ok(service.getStatementByFilter(filter));
    }

    @GetMapping("{statementId}")
    public ResponseEntity<?> getStatementById(@PathVariable("statementId") long statementId) {
        return ResponseEntity.ok(service.getStatementById(statementId));
    }

    @PostMapping("/{statementId}/accept")
    public ResponseEntity<?> acceptStatement(@PathVariable("statementId") long statementId) {
        service.acceptStatement(statementId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{statementId}/dismiss")
    public ResponseEntity<?> dismissStatement(@PathVariable("statementId") long statementId) {
        service.dismissStatement(statementId);
        return ResponseEntity.ok().build();
    }
}
