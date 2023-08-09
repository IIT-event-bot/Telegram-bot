package com.project.userService.models;

public record StatementDto(
        long id,
        String name,
        String surname,
        String patronymic,
        boolean isChecked,
        long userId,
        String groupName
) {
}
