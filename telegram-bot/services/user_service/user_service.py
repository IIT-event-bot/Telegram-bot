from model.student import Student
from model.user import User


class UserService:
    def get_user_by_id(self, user_id: int) -> User:
        raise NotImplementedError

    def get_student_by_user_id(self, user_id: int) -> Student:
        raise NotImplementedError

    def is_student(self, user_id: int) -> bool:
        raise NotImplementedError
