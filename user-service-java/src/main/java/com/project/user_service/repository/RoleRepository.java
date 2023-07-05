package com.project.user_service.repository;

import com.project.user_service.models.Role;
import com.project.user_service.models.RoleDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleDto, Long> {
    RoleDto getRoleDtoById(long id);

    RoleDto getRoleDtoByRole(Role role);
}
