class UserService:
    def get_user_by_id(self, id: int):
        raise NotImplementedError

    def get_student_by_user_id(self, user_id: int):
        raise NotImplementedError

    def is_student(self, user_id: int) -> bool:
        raise NotImplementedError
