package com.project.scheduleService.models;

public enum WeekType {
    FIRST_WEEK,
    SECOND_WEEK;
    private static final WeekType[] TYPES = WeekType.values();

    public static WeekType get(int weekNumber) {
        if (weekNumber > 1 || weekNumber < 0) {
            throw new IllegalArgumentException("Wrong week number. Week number must be between 0 and 1");
        }
        return TYPES[weekNumber];
    }
}
