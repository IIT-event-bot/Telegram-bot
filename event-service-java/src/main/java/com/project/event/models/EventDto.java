package com.project.event.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class EventDto {
    private long id;
    private String title;
    private String text;
    private boolean hasFeedback;
    private LocalDateTime eventTime;
    private boolean isGroupEvent;
    private boolean isStudentEvent;
    private String type;
    private List<Long> groupsIds;
    private List<Long> studentsIds;
}
