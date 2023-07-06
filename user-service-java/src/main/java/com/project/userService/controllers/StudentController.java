package com.project.userService.controllers;

import com.project.userService.models.Student;
import com.project.userService.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService service;

    @Autowired
    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAllStudents(@RequestParam(value = "group", required = false) String groupName) {
        return ResponseEntity.ok(service.getStudentsByGroup(groupName));
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<?> getStudentById(@PathVariable("studentId") long studentId) {
        return ResponseEntity.ok(service.getStudentById(studentId));
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<?> updateStudent(@PathVariable("studentId") long studentId,
                                           @RequestBody Student student) {
        student.setId(studentId);
        service.updateStudent(student);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<?> deleteStudentById(@PathVariable("studentId") long studentId) {
        service.deleteStudentById(studentId);
        return ResponseEntity.ok().build();
    }
}
