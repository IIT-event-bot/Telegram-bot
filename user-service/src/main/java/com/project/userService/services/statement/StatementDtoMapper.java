package com.project.userService.services.statement;

import com.project.userService.models.Statement;
import com.project.userService.models.StatementDto;

@FunctionalInterface
public interface StatementDtoMapper {
    StatementDto convert(Statement statement);
}
