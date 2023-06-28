from ..models.statement import Statement
from ..models.user import User
from ..repositories.user_repository import UserRepository


class UserService:
    def __init__(self, repository: UserRepository) -> None:
        self.__repository = repository

    def get_all_users(self) -> list[User]:
        pass

    def save_user(self, user: User):
        self.__repository.save_user(user)

    def register(self, statement: Statement) -> None:
        pass

    def accept_student(self, statement: Statement) -> None:
        pass

    def dismiss_user(self, statement_id: int) -> None:
        pass

    def create_manager(self, user_id: int) -> None:
        pass

    def delete_manager(self, user_id: int) -> None:
        pass
