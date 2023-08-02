package com.project.userService.models;

public record StudentDto(long id,
                         String name,
                         String surname,
                         String patronymic,
                         String group,
                         String username) {
}
