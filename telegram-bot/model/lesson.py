import json
from datetime import time


class Lesson:
    def __init__(self, lesson_id: int,
                 title: str,
                 teacher: str,
                 auditorium: str,
                 time_start: time,
                 time_end: time,
                 week_type: str,
                 day_type: str,
                 group_id: str) -> None:
        self.lesson_id = lesson_id
        self.title = title
        self.teacher = teacher
        self.auditorium = auditorium
        self.time_start = time_start
        self.time_end = time_end
        self.week_type = week_type
        self.day_type = day_type
        self.group_id = group_id

    lesson_id: int
    title: str
    teacher: str
    auditorium: str
    time_start: time
    time_end: time
    week_type: str
    day_type: str
    group_id: str

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True,
                          indent=4)

    @staticmethod
    def fromJSON(body):
        lesson_json = json.loads(body)
        return Lesson(
            lesson_id=lesson_json['lesson_id'],
            title=lesson_json['title'],
            teacher=lesson_json['teacher'],
            auditorium=lesson_json['auditorium'],
            time_start=lesson_json['time_start'],
            time_end=lesson_json['time_end'],
            week_type=lesson_json['week_type'],
            day_type=lesson_json['day_type'],
            group_id=lesson_json['group_id'])
