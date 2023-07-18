package com.project.userService.utils;

import com.project.userService.services.session.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private final SessionService service;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        var user = service.getUserByToken((String) targetDomainObject);
        return user.getRole().getName().name().equals(permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
