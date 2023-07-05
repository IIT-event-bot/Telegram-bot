package com.project.user_service.services;

import com.project.user_service.models.Role;
import com.project.user_service.models.User;
import com.project.user_service.repository.RoleRepository;
import com.project.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final RoleRepository roleRepository;

    @Override
    public User getUserById(long id) {
        return repository.getUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public void updateUserRole(long userId, Role role) {
        var user = repository.getUserById(userId);
        var savedRole = roleRepository.getRoleDtoByRole(role);
        user.setRole(savedRole);
        repository.save(user);
    }
}
