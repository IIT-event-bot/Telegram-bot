package com.project.scheduleService.models;

public enum DayType {
    MONDAY("Понедельник"),
    TUESDAY("Вторник"),
    WEDNESDAY("Среда"),
    THURSDAY("Четверг"),
    FRIDAY("Пятница"),
    SATURDAY("Суббота"),
    SUNDAY("Воскресенье")
    ;
    public final String title;

    DayType(String title) {
        this.title = title;
    }
}
