import os

import grpc

import proto.studentService_pb2
import proto.studentService_pb2_grpc
import proto.userService_pb2
import proto.userService_pb2_grpc
from user_service.user_service import UserService


class GrpcUserService(UserService):
    def get_user_by_id(self, user_id: int):
        with grpc.insecure_channel(
                f'{os.environ["GRPC_USERSERVICE_HOST"]}:{os.environ["GRPC_USERSERVICE_PORT"]}') as channel:
            stub = proto.userService_pb2_grpc.UserServiceStub(channel)
            request = proto.userService_pb2.UserRequest(id=user_id)
            response = stub.getUserById(request)
            return response

    def get_student_by_user_id(self, user_id: int):
        with grpc.insecure_channel(
                f'{os.environ["GRPC_USERSERVICE_HOST"]}:{os.environ["GRPC_USERSERVICE_PORT"]}') as channel:
            stub = proto.studentService_pb2_grpc.StudentServiceStub(channel)
            request = proto.studentService_pb2.UserRequest(id=user_id)
            response = stub.getStudentByUserId(request)
            return response
