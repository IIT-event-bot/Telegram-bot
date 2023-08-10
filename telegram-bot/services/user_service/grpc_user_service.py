import os

import grpc

import services.grpc_service.studentService_pb2
import services.grpc_service.studentService_pb2_grpc
import services.grpc_service.userService_pb2
import services.grpc_service.userService_pb2_grpc
from model.student import Student
from model.user import User
from services.group_service.group_service import GroupService
from services.user_repository.redis_repository import UserRepository
from services.user_service.user_service import UserService


class GrpcUserService(UserService):
    def __init__(self, repo: UserRepository, group_service: GroupService) -> None:
        self.repo = repo
        self.group_service = group_service

    def get_user_by_id(self, user_id: int):
        user = self.repo.get_user_by_id(user_id)
        if user is not None:
            return user
        user = self.get_user_by_id_grpc(user_id)
        dto = User(user_id=user.id, username=user.username, role=user.role)
        self.repo.save_user(dto)
        return user

    def is_student(self, user_id: int) -> bool:
        user = self.get_user_by_id(user_id)
        return user.role == 'STUDENT'

    def get_student_by_user_id(self, user_id: int):
        if not self.is_student(user_id):
            return None
        student = self.repo.get_student_by_user_id(user_id)
        if student is not None:
            return student
        student = self.get_student_by_user_id_grpc(user_id)
        group = self.group_service.get_group_by_title(student.groupName)
        dto = Student(student_id=student.id,
                      user_id=user_id,
                      name=student.name,
                      surname=student.surname,
                      patronymic=student.patronymic,
                      group_name=group.title,
                      group_id=group.id)
        self.repo.save_student(dto)
        return student

    @classmethod
    def get_student_by_user_id_grpc(cls, user_id: int):
        with grpc.insecure_channel(
                f'{os.environ["GRPC_USERSERVICE_HOST"]}:{os.environ["GRPC_USERSERVICE_PORT"]}') as channel:
            stub = services.grpc_service.studentService_pb2_grpc.StudentServiceStub(channel)
            request = services.grpc_service.studentService_pb2.UserRequest(id=user_id)
            response = stub.getStudentByUserId(request)
            return response

    @classmethod
    def get_user_by_id_grpc(cls, user_id: int):
        with grpc.insecure_channel(
                f'{os.environ["GRPC_USERSERVICE_HOST"]}:{os.environ["GRPC_USERSERVICE_PORT"]}') as channel:
            stub = services.grpc_service.userService_pb2_grpc.UserServiceStub(channel)
            request = services.grpc_service.userService_pb2.UserRequest(id=user_id)
            response = stub.getUserById(request)
            return response
