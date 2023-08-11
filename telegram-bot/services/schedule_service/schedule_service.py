class ScheduleService:
    def get_schedule_on_week(self, group_id: int):
        raise NotImplementedError

    def get_schedule_today(self, group_id: int):
        raise NotImplementedError

    def get_schedule_tomorrow(self, group_id: int):
        raise NotImplementedError
