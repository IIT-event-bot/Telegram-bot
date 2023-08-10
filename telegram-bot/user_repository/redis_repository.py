import json
from datetime import timedelta

import redis

from model.student import Student
from model.user import User
from user_repository.user_repository import UserRepository

USER_BUCKET_PREFIX = 'usr'
STUDENT_BUCKET_PREFIX = 'std'


class RedisRepository(UserRepository):
    def __init__(self, redis_repo: redis.Redis) -> None:
        self.redis = redis_repo

    def get_user_by_id(self, user_id: int):
        key = f'{USER_BUCKET_PREFIX}:{user_id}'
        saved = self.redis.get(key)
        if saved is None:
            return None
        user_json = json.loads(saved)
        self.redis.expire(name=key, time=timedelta(hours=1))
        return User(user_id=user_json['user_id'], username=user_json['username'], role=user_json['role'])

    def save_user(self, user):
        key = f'{USER_BUCKET_PREFIX}:{user.user_id}'
        self.redis.append(key, user.toJSON())
        self.redis.expire(name=key, time=timedelta(hours=1))

    def get_student_by_user_id(self, user_id: int):
        key = f'{STUDENT_BUCKET_PREFIX}:{user_id}'
        saved = self.redis.get(key)
        if saved is None:
            return None
        student_json = json.loads(saved)
        self.redis.expire(name=key, time=timedelta(hours=1))
        return Student(student_id=student_json['student_id'],
                       user_id=student_json['user_id'],
                       name=student_json['name'],
                       surname=student_json['surname'],
                       patronymic=student_json['patronymic'],
                       group_name=student_json['group_name'])

    def save_student(self, student):
        key = f'{STUDENT_BUCKET_PREFIX}:{student.user_id}'
        self.redis.append(key, student.toJSON())
        self.redis.expire(name=key, time=timedelta(hours=1))
