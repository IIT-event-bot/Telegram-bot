from repositories.user_repository.user_repository import UserCacheRepository
from services.user_service.user_service import UserService


class ProxyCachedUserService(UserService):
    def __init__(self, user_service: UserService, repository: UserCacheRepository) -> None:
        self.service = user_service
        self.cache = repository

    def get_user_by_id(self, user_id: int):
        user = self.cache.get_user_by_id(user_id)
        if user is not None:
            return user
        user = self.service.get_user_by_id(user_id)
        self.cache.save_user(user)
        return user

    def is_student(self, user_id: int) -> bool:
        user = self.cache.get_user_by_id(user_id)
        if user is not None:
            return user.role == 'STUDENT'
        user = self.get_user_by_id(user_id)
        return user.role == 'STUDENT'

    def get_student_by_user_id(self, user_id: int):
        if not self.is_student(user_id):
            return None
        student = self.cache.get_student_by_user_id(user_id)
        if student is not None:
            return student
        student = self.service.get_student_by_user_id(user_id)
        self.cache.save_student(student)
        return student
