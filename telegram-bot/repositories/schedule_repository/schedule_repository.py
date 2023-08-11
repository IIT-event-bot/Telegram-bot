class ScheduleCacheRepository:
    def get_today_schedule(self, group_id: int):
        raise NotImplementedError

    def save_today_schedule(self, group_id, schedule):
        raise NotImplementedError

    def get_tomorrow_schedule(self, group_id: int):
        raise NotImplementedError

    def save_tomorrow_schedule(self, group_id, schedule):
        raise NotImplementedError

    def get_week_schedule(self, group_id: int):
        raise NotImplementedError

    def save_week_schedule(self, group_id, schedule):
        raise NotImplementedError
