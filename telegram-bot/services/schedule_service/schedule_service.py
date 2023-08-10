class ScheduleService:
    def get_schedule_by_group_id(self, group_id: int):
        raise NotImplementedError

    def get_schedule_today(self, group_id):
        raise NotImplementedError
