package com.project.userService.controllers;

import com.project.userService.models.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> illegalArgumentHandler(IllegalArgumentException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> othersExceptionHandler(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().build();
    }
}
