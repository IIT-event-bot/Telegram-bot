package com.project.userService.services;

import com.project.userService.models.Role;
import com.project.userService.models.Statement;
import com.project.userService.models.Student;
import com.project.userService.repository.StatementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementServiceImpl implements StatementService {
    private final StatementRepository repository;
    private final UserService userService;
    private final StudentService studentService;

    @Override
    public Statement getStatementById(long id) {
        return repository.getStatementById(id);
    }

    @Override
    public List<Statement> getAllStatements() {
        return repository.findAll();
    }

    @Override
    public List<Statement> getStatementByFilter(String filter) {
        if (filter == null) {
            return this.getAllStatements();
        }
        if (!filter.equals("checked") && !filter.equals("unchecked")) {
            throw new IllegalArgumentException("Wrong filter");
        }
        boolean isChecked = filter.equals("checked");
        return repository.getStatementByChecked(isChecked);
    }

    @Override
    @Transactional
    public void acceptStatement(long statementId) {
        var statement = repository.getStatementById(statementId);
        statement.setChecked(true);
        repository.save(statement);
        userService.updateUserRole(statement.getUserId(), Role.STUDENT);
        var student = createStudentByStatement(statement);
        studentService.saveStudent(student);
    }

    @Override
    public void dismissStatement(long statementId) {
        var statement = repository.getStatementById(statementId);
        statement.setChecked(true);
        repository.save(statement);
    }

    private Student createStudentByStatement(Statement statement) {
        return new Student(statement.getId(),
                statement.getName(),
                statement.getSurname(),
                statement.getPatronymic(),
                statement.getGroupId(),
                statement.getUserId());
    }
}
