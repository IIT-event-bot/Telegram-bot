package com.project.userService.services;

import com.project.userService.models.User;

public interface SessionService {
    String save(long userId, String token, String username);

    User getUserByToken(String token);

    boolean isValid(String token);
}
