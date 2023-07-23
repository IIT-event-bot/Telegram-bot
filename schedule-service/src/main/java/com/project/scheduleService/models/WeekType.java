package com.project.scheduleService.models;

public enum WeekType {
    FIRST_WEEK("Первая неделя"),
    SECOND_WEEK("Вторая неделя")
    ;
    public final String title;

    WeekType(String title) {
        this.title = title;
    }
}
