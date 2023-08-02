package com.project.event.services.event;

import com.project.event.models.Event;
import com.project.event.models.utils.NotificationType;
import com.project.event.repositories.EventRepository;
import com.project.event.services.notification.TelegramEventNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class EventScheduleService {
    private final EventRepository repository;
    private final TelegramEventNotificationService notificationService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendEventsToday() {
        var now = LocalDateTime.now();
        var dayEnd = now.plusDays(1);
        var eventsToday = repository.getEventsByEventTimeBetween(now, dayEnd);
        notificationService.sendEvents(eventsToday);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendRepeatEvent() {
        var now = LocalDateTime.now();
        var dayEnd = now.plusDays(1);
        var eventsRepeat = repository.getEventsByRepeatTimeBetween(now, dayEnd);
        var allRepeats = getAllRepeatEvents(eventsRepeat);
        notificationService.sendEvents(allRepeats);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendFeedback() {
        var now = LocalDateTime.now();
        var dayAgo = now.minusDays(1);
        var feedback = repository.getEventByFeedbackTime(dayAgo, now);
        for (var event : feedback) {
            var feedbackTime = event.getEventTime().plusDays(1);
            event.setEventTime(feedbackTime);
            var eventText = "Вчера прошло событие '" + event.getTitle() + "', оставьте, пожалуйста обратную связь по этому событию";
            event.setText(eventText);
            event.setType(NotificationType.FEEDBACK);
        }
        notificationService.sendEvents(feedback);
    }

    private List<Event> getAllRepeatEvents(List<Event> events) {
        var now = LocalDateTime.now();
        var dayEnd = now.plusDays(1);
        List<Event> allRepeats = new ArrayList<>();
        for (Event event : events) {
            for (var repeatTime : event.getRepeatTime()) {
                if (repeatTime.isAfter(now) && repeatTime.isBefore(dayEnd)) {
                    var repeatEvent = new Event();
                    repeatEvent.setId(event.getId());
                    repeatEvent.setStudentEvent(event.isStudentEvent());
                    repeatEvent.setStudents(event.getStudents());
                    repeatEvent.setGroupEvent(event.isGroupEvent());
                    repeatEvent.setGroups(event.getGroups());
                    repeatEvent.setEventTime(repeatTime);
                    repeatEvent.setTitle(event.getTitle());
                    repeatEvent.setText(event.getText());

                    allRepeats.add(repeatEvent);
                }
            }
        }
        return allRepeats;
    }
}
