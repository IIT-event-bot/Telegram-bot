from datetime import timedelta

import redis

from model.student import Student
from model.user import User
from repositories.user_repository.user_repository import UserCacheRepository

USER_BUCKET_PREFIX = 'usr'
STUDENT_BUCKET_PREFIX = 'stdt'


class RedisUserCacheRepository(UserCacheRepository):
    def __init__(self, redis_repo: redis.Redis) -> None:
        self.redis = redis_repo

    def get_user_by_id(self, user_id: int):
        key = f'{USER_BUCKET_PREFIX}:{user_id}'
        saved = self.redis.get(key)
        if saved is None:
            return None
        self.redis.expire(name=key, time=timedelta(hours=1))
        return User.fromJSON(saved)

    def save_user(self, user):
        key = f'{USER_BUCKET_PREFIX}:{user.user_id}'
        self.redis.append(key, user.toJSON())
        self.redis.expire(name=key, time=timedelta(hours=1))

    def get_student_by_user_id(self, user_id: int):
        key = f'{STUDENT_BUCKET_PREFIX}:{user_id}'
        saved = self.redis.get(key)
        if saved is None:
            return None
        self.redis.expire(name=key, time=timedelta(hours=1))
        return Student.fromJSON(saved)

    def save_student(self, student):
        key = f'{STUDENT_BUCKET_PREFIX}:{student.user_id}'
        self.redis.append(key, student.toJSON())
        self.redis.expire(name=key, time=timedelta(hours=1))
