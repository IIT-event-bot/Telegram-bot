import os
from datetime import datetime

import grpc

import services.grpc_service.scheduleService_pb2
import services.grpc_service.scheduleService_pb2_grpc
from model.lesson import Lesson
from services.schedule_service.schedule_service import ScheduleService


class GrpcScheduleService(ScheduleService):
    def get_schedule_today(self, group_id: int):
        with grpc.insecure_channel(
                f'{os.environ["GRPC_SCHEDULESERVICE_HOST"]}:{os.environ["GRPC_SCHEDULESERVICE_PORT"]}') as channel:
            stub = services.grpc_service.scheduleService_pb2_grpc.ScheduleServiceStub(channel)
            request = services.grpc_service.scheduleService_pb2.ScheduleRequest(groupId=group_id)
            lessons = []
            for lesson in stub.getScheduleToday(request):
                lessons.append(lesson)

            return self.__convert_to_dto(lessons)

    def get_schedule_on_week(self, group_id: int):
        with grpc.insecure_channel(
                f'{os.environ["GRPC_SCHEDULESERVICE_HOST"]}:{os.environ["GRPC_SCHEDULESERVICE_PORT"]}') as channel:
            stub = services.grpc_service.scheduleService_pb2_grpc.ScheduleServiceStub(channel)
            request = services.grpc_service.scheduleService_pb2.ScheduleRequest(groupId=group_id)
            lessons = []
            for lesson in stub.getScheduleWeek(request):
                lessons.append(lesson)

            return self.__convert_to_dto(lessons)

    def get_schedule_tomorrow(self, group_id: int):
        with grpc.insecure_channel(
                f'{os.environ["GRPC_SCHEDULESERVICE_HOST"]}:{os.environ["GRPC_SCHEDULESERVICE_PORT"]}') as channel:
            stub = services.grpc_service.scheduleService_pb2_grpc.ScheduleServiceStub(channel)
            request = services.grpc_service.scheduleService_pb2.ScheduleRequest(groupId=group_id)
            lessons = []
            for lesson in stub.getScheduleTomorrow(request):
                lessons.append(lesson)

            return self.__convert_to_dto(lessons)

    @classmethod
    def __convert_to_dto(cls, schedule: list):
        lessons = []
        for lesson in schedule:
            lessons.append(Lesson(
                lesson_id=lesson.id,
                title=lesson.title,
                teacher=lesson.teacher,
                auditorium=lesson.auditorium,
                time_start=datetime.fromtimestamp(lesson.timeStart.seconds).time(),
                time_end=datetime.fromtimestamp(lesson.timeEnd.seconds).time(),
                week_type=lesson.weekType,
                day_type=lesson.dayType,
                group_id=lesson.groupId))

        lessons.sort(key=lambda l: l.timeStart)
        return lessons
