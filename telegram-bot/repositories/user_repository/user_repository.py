class UserRepository:
    def get_user_by_id(self, user_id: int):
        raise NotImplementedError

    def save_user(self, user):
        raise NotImplementedError

    def get_student_by_user_id(self, user_id: int):
        raise NotImplementedError

    def save_student(self, student):
        raise NotImplementedError
