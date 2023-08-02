package com.project.event.services.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ErrorNotificationService implements TelegramNotificationService {
    @Override
    public void sendNotification(long chatId, String title, String text, LocalDateTime sendTime) {

    }
}
