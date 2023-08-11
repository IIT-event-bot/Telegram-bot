import datetime
import json

import redis

from model.lesson import Lesson
from repositories.schedule_repository.schedule_repository import ScheduleCacheRepository

TODAY_SCHEDULE_PATTERN = 'td:{date}:{group_id}'
TOMORROW_SCHEDULE_PATTERN = 'tm:{date}:{group_id}'
WEEK_SCHEDULE_PATTERN = 'wk:{date}:{group_id}'


class RedisScheduleCacheRepository(ScheduleCacheRepository):
    def __init__(self, redis_repo: redis.Redis) -> None:
        self.repo = redis_repo

    def get_today_schedule(self, group_id: int):
        key = TODAY_SCHEDULE_PATTERN.format(date=datetime.datetime.now().date().strftime("%d-%m-%Y"), group_id=group_id)
        return self.__get_schedule_list(key)

    def save_today_schedule(self, group_id, schedule: list[Lesson]):
        key = TODAY_SCHEDULE_PATTERN.format(date=datetime.datetime.now().date().strftime("%d-%m-%Y"), group_id=group_id)
        self.__save_schedule_list(key, schedule)

    def get_tomorrow_schedule(self, group_id: int):
        key = TOMORROW_SCHEDULE_PATTERN.format(date=datetime.datetime.now().date().strftime("%d-%m-%Y"),
                                               group_id=group_id)
        return self.__get_schedule_list(key)

    def save_tomorrow_schedule(self, group_id, schedule):
        key = TOMORROW_SCHEDULE_PATTERN.format(date=datetime.datetime.now().date().strftime("%d-%m-%Y"),
                                               group_id=group_id)
        self.__save_schedule_list(key, schedule)

    def get_week_schedule(self, group_id: int):
        key = WEEK_SCHEDULE_PATTERN.format(date=datetime.datetime.now().date().strftime("%d-%m-%Y"),
                                           group_id=group_id)
        saved = self.repo.get(key)
        if saved is None:
            return None
        self.repo.expire(name=key, time=datetime.timedelta(hours=6))
        schedule = json.loads(saved)
        cache = dict[str, list[str]]()
        for day in schedule:
            cache[day] = []
            for lesson in schedule[day]:
                cache[day].append(Lesson.fromJSON(lesson))
        return cache

    def save_week_schedule(self, group_id, schedule):
        key = WEEK_SCHEDULE_PATTERN.format(date=datetime.datetime.now().date().strftime("%d-%m-%Y"),
                                           group_id=group_id)
        cache = dict[str, list[str]]()
        for day in schedule:
            cache[day] = []
            for lesson in schedule[day]:
                cache[day].append(lesson.toJSON())
        self.repo.append(key, json.dumps(cache))
        self.repo.expire(name=key, time=datetime.timedelta(hours=6))

    def __get_schedule_list(self, key):
        saved = self.repo.get(name=key)
        if saved is None:
            return None
        self.repo.expire(name=key, time=datetime.timedelta(hours=6))
        lesson_list = json.loads(saved, strict=False)
        lessons = list[Lesson]()
        for lesson in lesson_list:
            lessons.append(Lesson.fromJSON(lesson))
        return lessons

    def __save_schedule_list(self, key, schedule):
        cache = []
        for lesson in schedule:
            cache.append(lesson.toJSON())
        self.repo.append(key=key, value=json.dumps(cache))
        self.repo.expire(name=key, time=datetime.timedelta(hours=6))
