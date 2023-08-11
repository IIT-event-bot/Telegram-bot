import datetime
import json

import redis

from model.lesson import Lesson
from repositories.schedule_repository.schedule_repository import ScheduleCacheRepository

TODAY_SCHEDULE_PREFIX = 'td:{date}:{group_id}'


class RedisScheduleCacheRepository(ScheduleCacheRepository):
    def __init__(self, redis_repo: redis.Redis) -> None:
        self.repo = redis_repo

    def get_today_schedule(self, group_id: int):
        key = TODAY_SCHEDULE_PREFIX.format(date=datetime.datetime.now().date().strftime("%d-%m-%Y"), group_id=group_id)
        saved = self.repo.get(name=key)
        if saved is None:
            return None
        self.repo.expire(name=key, time=datetime.timedelta(hours=6))
        data = saved.decode('utf-8')
        lesson_list = json.loads(data, strict=False)
        lessons = list[Lesson]()
        for lesson in lesson_list:
            lessons.append(Lesson.fromJSON(lesson))
        return lessons

    def save_today_schedule(self, group_id, schedule: list[Lesson]):
        key = TODAY_SCHEDULE_PREFIX.format(date=datetime.datetime.now().date().strftime("%d-%m-%Y"), group_id=group_id)
        cache = []
        for lesson in schedule:
            cache.append(lesson.toJSON())
        self.repo.append(key=key, value=json.dumps(cache))
        self.repo.expire(name=key, time=datetime.timedelta(hours=6))
