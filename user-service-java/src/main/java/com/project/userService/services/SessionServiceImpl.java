package com.project.userService.services;

import com.project.userService.models.Session;
import com.project.userService.models.User;
import com.project.userService.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {
    private final SessionRepository repository;
    private final UserService userService;

    @Override
    public String save(long userId, String token, String username) {
        var user = userService.getUserById(userId);
        if (!user.getUsername().equals(username)) {
            throw new IllegalArgumentException("Username not equals saved");
        }
        var now = LocalDateTime.now();
        var expiredTime = now.plusHours(1);
        expireUserSession(userId);
        var session = new Session(0L, token, expiredTime, now, user, false);
        return repository.save(session).getToken();
    }

    @Override
    public User getUserByToken(String token) {
        return repository.getNotExpiredSessionByToken(token).getUser();
    }

    @Override
    public boolean isValid(String token) {
        var session = repository.getNotExpiredSessionByToken(token);
        if (session == null) {
            return false;
        }
        var now = LocalDateTime.now();
        if (session.getExpiredTime().isBefore(now)) {
            expireUserSession(session.getUser().getId());
            return false;
        }
        return true;
    }

    private void expireUserSession(long userId) {
        var sessions = repository.getNotExpiredSessionByUserId(userId);
        for (Session session : sessions) {
            session.setExpired(true);
        }
        repository.saveAll(sessions);
    }
}
