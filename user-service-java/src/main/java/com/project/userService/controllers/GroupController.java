package com.project.userService.controllers;

import com.project.userService.models.Group;
import com.project.userService.services.GroupService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/group")
@Tag(name = "Group service")
public class GroupController {
    private final GroupService service;

    @Autowired
    public GroupController(GroupService service) {
        this.service = service;
    }

    @Operation(summary = "Получение всех групп")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = Group.class
                                            )
                                    )
                            )
                    }
            )
    })
    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @GetMapping
    public ResponseEntity<?> getAllGroups(@CookieValue("session-token") String token,
                                          @RequestParam(value = "title", required = false) String title) {
        return ResponseEntity.ok(service.getGroupsLikeTitle(title));
    }

    @Operation(summary = "Получение группы по id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Group.class
                                    )
                            )
                    }
            )
    })
    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupById(@CookieValue("session-token") String token,
                                          @PathVariable("groupId") long groupId) {
        return ResponseEntity.ok(service.getGroupById(groupId));
    }

    @Operation(summary = "Создание группы",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(
                                    implementation = Group.class
                            )
                    )
            ))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201"
            )
    })
    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @PostMapping
    public ResponseEntity<?> createGroup(@CookieValue("session-token") String token,
                                         @RequestBody Group group) {
        service.createGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Обновление группы по id",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(
                                    implementation = Group.class
                            )
                    )
            ))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            )
    })
    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroup(@CookieValue("session-token") String token,
                                         @PathVariable("groupId") long groupId,
                                         @RequestBody Group group) {
        group.setId(groupId);
        service.updateGroup(group);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удаление группы")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            )
    })
    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroupById(@CookieValue("session-token") String token,
                                             @PathVariable("groupId") long groupId) {
        service.deleteGroupById(groupId);
        return ResponseEntity.ok().build();
    }
}
