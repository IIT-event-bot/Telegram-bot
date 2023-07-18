package com.project.userService.services;

import com.project.userService.models.Group;
import com.project.userService.models.Role;
import com.project.userService.models.Statement;
import com.project.userService.models.Student;
import com.project.userService.repository.StatementRepository;
import com.project.userService.services.notification.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatementServiceImpl implements StatementService {
    private final StatementRepository repository;
    private final UserService userService;
    private final StudentService studentService;
    private final GroupService groupService;
    private final TelegramNotificationService notificationService;

    @Override
    public Statement getStatementById(long id) {
        var statement = repository.getStatementById(id);
        var group = groupService.getGroupById(statement.getGroupId());
        statement.setGroupName(group.getTitle());
        return statement;
    }

    @Override
    public List<Statement> getAllStatements() {
        List<Statement> statements = repository.findAll();
        injectStatementGroupName(statements);
        return statements;
    }

    @Override
    public List<Statement> getStatementByFilter(String filter) {
        if (filter == null) {
            return this.getAllStatements();
        }
        if (!filter.equals("checked") && !filter.equals("unchecked")) {
            throw new IllegalArgumentException("Wrong filter '" + filter + "'");
        }
        boolean isChecked = filter.equals("checked");
        var statements = repository.getStatementByChecked(isChecked);
        injectStatementGroupName(statements);
        return statements;
    }

    private void injectStatementGroupName(List<Statement> statements) {
        List<Long> groupsIds = statements.stream().map(Statement::getGroupId).toList();
        List<Group> groups = groupService.getGroupsByIds(groupsIds);
        for (var statement : statements) {
            var group = groups.stream().filter(x -> x.getId() == statement.getGroupId()).findFirst().get();
            statement.setGroupName(group.getTitle());
        }
    }

    @Override
    @Transactional
    public void acceptStatement(Statement statement) {
        var savedStatement = repository.getStatementById(statement.getId());
        if (savedStatement.isChecked()) {
            throw new IllegalArgumentException("Statement already checked");
        }
        Group group;
        if (statement.getGroupName() != null) {
            group = groupService.getGroupByTitle(statement.getGroupName());
            savedStatement.setGroupId(group.getId());
        } else {
            var savedGroup = savedStatement.getGroupId();
            group = groupService.getGroupById(savedGroup);
        }
        if (statement.getName() != null) {
            savedStatement.setName(statement.getName());
        }
        if (statement.getSurname() != null) {
            savedStatement.setSurname(statement.getSurname());
        }
        if (statement.getPatronymic() != null) {
            savedStatement.setPatronymic(statement.getPatronymic());
        }
        savedStatement.setChecked(true);
        repository.save(savedStatement);

        userService.updateUserRole(savedStatement.getUserId(), Role.STUDENT);
        var user = userService.getUserById(savedStatement.getUserId());

        var student = createStudentByStatement(savedStatement);
        studentService.saveStudent(student);

        notificationService.sendNotification(user.getId(),
                "Добавление в систему",
                savedStatement.getSurname() + " " + savedStatement.getName() + " " + savedStatement.getPatronymic() + ", " +
                        "вы были добавлены в систему информирования, " +
                        "в группу " + group.getTitle());
    }

    @Override
    @Transactional
    public void dismissStatement(long statementId) {
        var statement = repository.getStatementById(statementId);
        if (statement.isChecked()) {
            throw new IllegalArgumentException("Statement already checked");
        }
        statement.setChecked(true);
        repository.save(statement);

        var user = userService.getUserById(statement.getUserId());

        notificationService.sendNotification(user.getId(),
                "Отклонение заявки",
                "Ваша заявка была отклонена");
    }

    @Override
    public void saveStatement(Statement statement) {
        var savedStatement = repository.getUncheckedStatementByUserId(statement.getUserId());
        if (savedStatement != null && !statement.isChecked()) {
            throw new IllegalArgumentException("Statement is already exist");
        }
        var group = groupService.getGroupByTitle(statement.getGroupName());
        statement.setGroupId(group.getId());
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
