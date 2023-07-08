package com.project.userService.repository;

import com.project.userService.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User getUserById(long id);
}
