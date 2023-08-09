package com.project.userService.services.statement;

import com.project.userService.models.Statement;
import com.project.userService.models.StatementDto;

import java.util.List;

public interface StatementService {
    StatementDto getStatementById(long id);

    List<StatementDto> getAllStatements();

    List<StatementDto> getStatementByFilter(String filter);

    void acceptStatement(Statement statement);

    void dismissStatement(long statementId);

    void saveStatement(Statement statement);
}
