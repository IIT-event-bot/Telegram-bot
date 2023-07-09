package com.project.userService.controllers;

import com.project.userService.models.User;
import com.project.userService.services.UserService;
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
@RequestMapping("/api/user")
@CrossOrigin
@Tag(name = "User service")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(summary = "Получение всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = User.class
                                            )
                                    )
                            )
                    }
            )
    })
    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @GetMapping
    public ResponseEntity<?> getAllUsers(@CookieValue("session-token") String token) {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @Operation(summary = "Получение пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = User.class
                                    )
                            )
                    }
            )
    })
    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@CookieValue("session-token") String token,
                                         @PathVariable("userId") long id) {
        return ResponseEntity.ok(service.getUserById(id));
    }
}
