from repositories.schedule_repository.schedule_repository import ScheduleCacheRepository
from services.schedule_service.schedule_service import ScheduleService


class ProxyCacheScheduleRepository(ScheduleService):
    def __init__(self, schedule_service: ScheduleService, repository: ScheduleCacheRepository) -> None:
        self.schedule_service = schedule_service
        self.cache = repository

    def get_schedule_today(self, group_id: int):
        lessons = self.cache.get_today_schedule(group_id=group_id)
        if lessons is not None:
            return lessons
        lessons = self.schedule_service.get_schedule_today(group_id=group_id)
        self.cache.save_today_schedule(group_id, lessons)
        return lessons

    def get_schedule_tomorrow(self, group_id: int):
        lessons = self.cache.get_tomorrow_schedule(group_id=group_id)
        if lessons is not None:
            return lessons
        lessons = self.schedule_service.get_schedule_tomorrow(group_id=group_id)
        self.cache.save_tomorrow_schedule(group_id, lessons)
        return lessons

    def get_schedule_on_week(self, group_id: int):
        lessons = self.cache.get_week_schedule(group_id=group_id)
        if lessons is not None:
            return lessons
        lessons = self.schedule_service.get_schedule_on_week(group_id=group_id)
        self.cache.save_week_schedule(group_id, lessons)
        return lessons
