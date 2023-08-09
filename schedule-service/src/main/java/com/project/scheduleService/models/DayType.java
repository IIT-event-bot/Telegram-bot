package com.project.scheduleService.models;

public enum DayType {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    private static final DayType[] TYPES = DayType.values();

    public static DayType get(int dayNumber) {
        if (dayNumber < 0 || dayNumber > 6) {
            throw new IllegalArgumentException("Wrong day number. Day number must be between 0 and 6");
        }
        return TYPES[dayNumber];
    }
}
