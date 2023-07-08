package com.project.event.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.event.config.RabbitConfig;
import com.project.event.models.Event;
import com.project.event.models.EventGrade;
import com.project.event.models.Feedback;
import com.project.event.models.RabbitMessage;
import com.project.event.repositories.EventGradeRepository;
import com.project.event.repositories.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
@EnableRabbit
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final EventGradeRepository gradeRepository;

    @Override
    public EventGrade getEventGrade(long eventId) {
        var grades = gradeRepository.getEventGradesByEventId(eventId);
        if (grades.size() == 0){
            var grade = new EventGrade();
            grade.setGrade(0);
            return grade;
        }
        var avg = grades.stream().mapToDouble(EventGrade::getGrade).average().getAsDouble();
        return new EventGrade(0, avg, grades.get(0).getEvent());
    }

    @Override
    public List<Feedback> getEventFeedback(long eventId) {
        return feedbackRepository.getFeedbacksByEventId(eventId);
    }

    @RabbitListener(queues = RabbitConfig.USER_SERVICE_QUEUE)
    public void saveFeedback(Message message) {
        ObjectMapper mapper = new ObjectMapper();
        RabbitMessage rabbitMessage;
        try {
            rabbitMessage = mapper.readValue(message.getBody(), RabbitMessage.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }
        handleRabbitMessage(rabbitMessage);
    }

    private void handleRabbitMessage(RabbitMessage rabbitMessage) {
        switch (rabbitMessage.method()) {
            case ADD_GRADE -> addGrade(rabbitMessage.body());
            case ADD_FEEDBACK -> addFeedback(rabbitMessage.body());
        }
    }

    private void addFeedback(Map<String, Object> feedbackJson) {
        var event = new Event();
        event.setId(Long.parseLong(feedbackJson.get("event_id").toString()));
        var feedback = new Feedback(0L, (String) feedbackJson.get("text"), event);

        feedbackRepository.save(feedback);
    }

    private void addGrade(Map<String, Object> gradeJson) {
        var event = new Event();
        event.setId(Long.parseLong(gradeJson.get("event_id").toString()));
        var grade = new EventGrade(0L, Long.parseLong(gradeJson.get("grade").toString()), event);

        gradeRepository.save(grade);
    }
}
