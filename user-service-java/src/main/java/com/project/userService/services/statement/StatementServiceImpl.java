package com.project.userService.services.statement;

import com.project.userService.models.*;
import com.project.userService.repository.StatementRepository;
import com.project.userService.services.group.GroupService;
import com.project.userService.services.student.StudentService;
import com.project.userService.services.user.UserService;
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
    private final StatementDtoMapper statementMapper;

    @Override
    public StatementDto getStatementById(long id) {
        var statement = repository.getStatementById(id);
        return statementMapper.convert(statement);
    }

    @Override
    public List<StatementDto> getAllStatements() {
        List<Statement> statements = repository.findAll();
        return statements
                .stream()
                .map(statementMapper::convert)
                .toList();
    }

    @Override
    public List<StatementDto> getStatementByFilter(String filter) {
        if (filter == null) {
            return this.getAllStatements();
        }
        if (!filter.equals("checked") && !filter.equals("unchecked")) {
            throw new IllegalArgumentException("Wrong filter '" + filter + "'");
        }
        boolean isChecked = filter.equals("checked");
        var statements = repository.getStatementByChecked(isChecked);
        return statements
                .stream()
                .map(statementMapper::convert)
                .toList();
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
        updateSavedStatement(savedStatement, statement);
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

    private void updateSavedStatement(Statement savedStatement, Statement newStatement) {
        if (newStatement.getName() != null) {
            savedStatement.setName(newStatement.getName());
        }
        if (newStatement.getSurname() != null) {
            savedStatement.setSurname(newStatement.getSurname());
        }
        if (newStatement.getPatronymic() != null) {
            savedStatement.setPatronymic(newStatement.getPatronymic());
        }
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
