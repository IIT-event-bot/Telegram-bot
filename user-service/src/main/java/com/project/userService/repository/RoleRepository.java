package com.project.userService.repository;

import com.project.userService.models.Role;
import com.project.userService.models.RoleDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleDto, Long> {
    RoleDto getRoleDtoById(long id);

    RoleDto getRoleDtoByName(Role role);
}
