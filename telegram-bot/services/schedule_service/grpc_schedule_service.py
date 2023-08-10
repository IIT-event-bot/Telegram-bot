import os

import grpc

import services.grpc_service.scheduleService_pb2
import services.grpc_service.scheduleService_pb2_grpc
from services.schedule_service.schedule_service import ScheduleService


class GrpcScheduleService(ScheduleService):
    def get_schedule_today(self, group_id):
        with grpc.insecure_channel(
                f'{os.environ["GRPC_SCHEDULESERVICE_HOST"]}:{os.environ["GRPC_SCHEDULESERVICE_PORT"]}') as channel:
            stub = services.grpc_service.scheduleService_pb2_grpc.ScheduleServiceStub(channel)
            request = services.grpc_service.scheduleService_pb2.ScheduleRequest(groupId=group_id)
            lessons = []
            for lesson in stub.getScheduleTodayByGroupId(request):
                lessons.append(lesson.title)

            return lessons
