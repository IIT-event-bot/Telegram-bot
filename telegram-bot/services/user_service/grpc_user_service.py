import os

import grpc

import services.grpc_service.studentService_pb2
import services.grpc_service.studentService_pb2_grpc
import services.grpc_service.userService_pb2
import services.grpc_service.userService_pb2_grpc
from model.student import Student
from model.user import User
from services.group_service.group_service import GroupService
from services.user_service.user_service import UserService


class GrpcUserService(UserService):
    def __init__(self, group_service: GroupService) -> None:
        self.group_service = group_service

    def is_student(self, user_id: int) -> bool:
        user = self.get_user_by_id(user_id)
        return user.role == 'STUDENT'

    def get_student_by_user_id(self, user_id: int) -> Student:
        with grpc.insecure_channel(
                f'{os.environ["GRPC_USERSERVICE_HOST"]}:{os.environ["GRPC_USERSERVICE_PORT"]}') as channel:
            stub = services.grpc_service.studentService_pb2_grpc.StudentServiceStub(channel)
            request = services.grpc_service.studentService_pb2.UserRequest(id=user_id)
            response = stub.getStudentByUserId(request)
            return self.__map_student(user_id=user_id, student=response)

    def get_user_by_id(self, user_id: int) -> User:
        with grpc.insecure_channel(
                f'{os.environ["GRPC_USERSERVICE_HOST"]}:{os.environ["GRPC_USERSERVICE_PORT"]}') as channel:
            stub = services.grpc_service.userService_pb2_grpc.UserServiceStub(channel)
            request = services.grpc_service.userService_pb2.UserRequest(id=user_id)
            response = stub.getUserById(request)
            return self.__map_user(response)

    @classmethod
    def __map_user(cls, user) -> User:
        return User(user_id=user.id, username=user.username, role=user.role)

    def __map_student(self, user_id, student) -> Student:
        group = self.group_service.get_group_by_title(student.group_name)
        return Student(student_id=student.id,
                       user_id=user_id,
                       name=student.name,
                       surname=student.surname,
                       patronymic=student.patronymic,
                       group_name=group.title,
                       group_id=group.id)
