package com.project.event.services;

import com.project.event.models.EventGrade;
import com.project.event.models.Feedback;

import java.util.List;

public interface FeedbackService {
    EventGrade getEventGrade(long eventId);
    List<Feedback> getEventFeedback(long eventId);
}
