package com.project.userService.services;

import com.project.userService.models.Role;
import com.project.userService.models.User;

import java.util.List;

public interface UserService {
    User getUserById(long id);

    List<User> getAllUsers();

    void updateUserRole(long userId, Role role);

    void saveUser(User user);
}
