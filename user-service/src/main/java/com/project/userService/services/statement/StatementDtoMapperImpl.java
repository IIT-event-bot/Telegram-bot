package com.project.userService.services.statement;

import com.project.userService.models.Statement;
import com.project.userService.models.StatementDto;
import com.project.userService.services.group.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatementDtoMapperImpl implements StatementDtoMapper {
    private final GroupService groupService;

    @Override
    public StatementDto convert(Statement statement) {
        var group = groupService.getGroupById(statement.getGroupId());
        return new StatementDto(
                statement.getId(),
                statement.getName(),
                statement.getSurname(),
                statement.getPatronymic(),
                statement.isChecked(),
                statement.getUserId(),
                group.getTitle()
        );
    }
}
