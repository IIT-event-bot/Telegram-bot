import os

import grpc

import services.grpc_service.groupService_pb2
import services.grpc_service.groupService_pb2_grpc
from services.group_service.group_service import GroupService


class GrpcGroupService(GroupService):
    def get_group_by_id(self, group_id: int):
        with grpc.insecure_channel(
                f'{os.environ["GRPC_USERSERVICE_HOST"]}:{os.environ["GRPC_USERSERVICE_PORT"]}') as channel:
            stub = services.grpc_service.groupService_pb2_grpc.GroupServiceStub(channel)
            request = services.grpc_service.groupService_pb2.GroupRequest(groupId=group_id)
            response = stub.getGroupByGroupId(request)
            return response

    def get_group_by_title(self, title: str):
        with grpc.insecure_channel(
                f'{os.environ["GRPC_USERSERVICE_HOST"]}:{os.environ["GRPC_USERSERVICE_PORT"]}') as channel:
            stub = services.grpc_service.groupService_pb2_grpc.GroupServiceStub(channel)
            request = services.grpc_service.groupService_pb2.GroupTitleRequest(title=title)
            response = stub.getGroupByTitle(request)
            return response
