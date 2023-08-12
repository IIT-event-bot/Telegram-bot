package com.project.userService.exception;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException() {
        super("User already exist");
    }

    public UserAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
