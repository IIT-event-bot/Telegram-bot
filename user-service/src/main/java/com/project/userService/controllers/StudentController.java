package com.project.userService.controllers;

import com.project.userService.models.Student;
import com.project.userService.models.StudentDto;
import com.project.userService.services.student.StudentService;
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

@RestController
@CrossOrigin
@RequestMapping("/api/student")
@Tag(name = "Student service")
public class StudentController {
    private final StudentService service;

    @Autowired
    public StudentController(StudentService service) {
        this.service = service;
    }

    @Operation(summary = "Получение всех студентов")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = StudentDto.class
                                            )
                                    )
                            )
                    }
            )
    })
//    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @GetMapping
    public ResponseEntity<?> getAllStudents(//@CookieValue("session-token") String token,
                                            @RequestParam(value = "group", required = false) String groupName) {
        return ResponseEntity.ok(service.getStudentsByGroup(groupName));
    }

    @Operation(summary = "Получение студента по id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = StudentDto.class
                                    )
                            )
                    }
            )
    })
//    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @GetMapping("/{studentId}")
    public ResponseEntity<?> getStudentById(//@CookieValue("session-token") String token,
                                            @PathVariable("studentId") long studentId) {
        return ResponseEntity.ok(service.getStudentById(studentId));
    }

    @Operation(summary = "Обновление студента по id",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(
                                    implementation = StudentDto.class
                            )
                    )
            ))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            )
    })
//    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @PutMapping("/{studentId}")
    public ResponseEntity<?> updateStudent(//@CookieValue("session-token") String token,
                                           @PathVariable("studentId") long studentId,
                                           @RequestBody Student student) {
        student.setId(studentId);
        service.updateStudent(student);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удаление студента")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200"
            )
    })
//    @PreAuthorize("hasPermission(#token, 'MANAGER')")
    @DeleteMapping("/{studentId}")
    public ResponseEntity<?> deleteStudentById(//@CookieValue("session-token") String token,
                                               @PathVariable("studentId") long studentId) {
        service.deleteStudentById(studentId);
        return ResponseEntity.ok().build();
    }
}
