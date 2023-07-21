package com.project.event.models;

import java.time.LocalDateTime;

public record EventDto(
        long id,
        String title,
        boolean hasFeedback,
        LocalDateTime eventTime,
        double grade
) {
}
