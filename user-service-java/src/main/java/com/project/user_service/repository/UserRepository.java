package com.project.user_service.repository;

import com.project.user_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User getUserById(long id);
}
