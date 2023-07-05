package com.project.user_service.services;

import com.project.user_service.models.Role;
import com.project.user_service.models.User;

import java.util.List;

public interface UserService {
    User getUserById(long id);

    List<User> getAllUsers();

    void updateUserRole(long userId, Role role);
}
