package com.project.userService.services;

import com.project.userService.models.Statement;

import java.util.List;

public interface StatementService {
    Statement getStatementById(long id);

    List<Statement> getAllStatements();

    List<Statement> getStatementByFilter(String filter);

    void acceptStatement(Statement statement);

    void dismissStatement(long statementId);

    void saveStatement(Statement statement);
}
