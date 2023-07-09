package com.project.userService.controllers;

import com.project.userService.models.Statement;
import com.project.userService.services.StatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/statement")
@Tag(name = "Statement service")
public class StatementController {
    private final StatementService service;

    @Autowired
    public StatementController(StatementService service) {
        this.service = service;
    }

    @Operation(summary = "Получение всех заявок")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = Statement.class
                                            )
                                    )
                            )
                    }
            )
    })
    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @GetMapping
    public ResponseEntity<?> getAllStatements(@CookieValue("session-token") String token,
                                              @RequestParam(value = "filter", required = false) String filter) {
        return ResponseEntity.ok(service.getStatementByFilter(filter));
    }

    @Operation(summary = "Получение заявки по id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Statement.class
                                    )
                            )
                    }
            )
    })
    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @GetMapping("{statementId}")
    public ResponseEntity<?> getStatementById(@CookieValue("session-token") String token,
                                              @PathVariable("statementId") long statementId) {
        return ResponseEntity.ok(service.getStatementById(statementId));
    }

    @Operation(summary = "Одобрение заявки",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(
                                    implementation = Statement.class
                            )
                    )
            ))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            )
    })
    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @PostMapping("/{statementId}/accept")
    public ResponseEntity<?> acceptStatement(@CookieValue("session-token") String token,
                                             @PathVariable("statementId") long statementId,
                                             @RequestBody Statement statement) {
        statement.setId(statementId);
        service.acceptStatement(statement);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Отклонение заявки")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            )
    })
    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @PostMapping("/{statementId}/dismiss")
    public ResponseEntity<?> dismissStatement(@CookieValue("session-token") String token,
                                              @PathVariable("statementId") long statementId) {
        service.dismissStatement(statementId);
        return ResponseEntity.ok().build();
    }
}
