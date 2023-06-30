from abc import ABC, abstractmethod

from core.deps import ComponentsContainer

from ..models.statement_dto import Statement

ioc = ComponentsContainer()


class StatementService(ABC):
    @abstractmethod
    def get_all_statements(self) -> list[Statement]:
        pass

    @abstractmethod
    def get_unchecked_statements(self) -> list[Statement]:
        pass

    @abstractmethod
    def accept_statement(self, statement) -> None:
        pass

    @abstractmethod
    def dismiss_statement(self, statement_id: int) -> None:
        pass


class StatementServiceImpl(StatementService):
    def __init__(self) -> None:
        self.__group_service = ioc.group_service
        self.__statement_service = ioc.statement_service
        self.__user_service = ioc.user_service

    def get_all_statements(self) -> list[Statement]:
        saved_statements = self.__statement_service.get_all_statement()
        return self.__inject_groups(saved_statements)

    def get_unchecked_statements(self) -> list[Statement]:
        saved_statements = self.__statement_service.get_unchecked_statement()
        return self.__inject_groups(statements=saved_statements)

    def accept_statement(self, statement) -> None:
        saved_statement = self.__statement_service.get_statement_by_id(statement_id=statement.id)
        if saved_statement.is_checked:
            raise Exception('Statement already checked')
        self.__user_service.accept_student(statement=statement)

    def dismiss_statement(self, statement_id: int) -> None:
        saved_statement = self.__statement_service.get_statement_by_id(statement_id=statement_id)
        if saved_statement.is_checked:
            raise Exception('Statement already checked')
        self.__user_service.dismiss_user(statement_id=statement_id)  # TODO нахождение группы

    def __inject_groups(self, statements) -> list[Statement]:
        result = []
        for statement in statements:
            group = self.__group_service.get_group_by_id(group_id=statement.group_id)
            result.append(Statement(id=statement.id,
                                    name=statement.name,
                                    surname=statement.surname,
                                    patronymic=statement.patronymic,
                                    group=group.title,
                                    is_checked=statement.is_checked))
        return result
