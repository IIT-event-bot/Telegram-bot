import os

import grpc

import proto.studentService_pb2
import proto.studentService_pb2_grpc
import proto.userService_pb2
import proto.userService_pb2_grpc
from model.student import Student
from model.user import User
from user_repository.redis_repository import UserRepository
from user_service.user_service import UserService


class GrpcUserService(UserService):
    def __init__(self, repo: UserRepository) -> None:
        self.repo = repo

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
        dto = Student(student_id=student.id,
                      user_id=user_id,
                      name=student.name,
                      surname=student.surname,
                      patronymic=student.patronymic,
                      group_name=student.groupName)
        self.repo.save_student(dto)
        return student

    @classmethod
    def get_student_by_user_id_grpc(cls, user_id: int):
        with grpc.insecure_channel(
                f'{os.environ["GRPC_USERSERVICE_HOST"]}:{os.environ["GRPC_USERSERVICE_PORT"]}') as channel:
            stub = proto.studentService_pb2_grpc.StudentServiceStub(channel)
            request = proto.studentService_pb2.UserRequest(id=user_id)
            response = stub.getStudentByUserId(request)
            return response

    @classmethod
    def get_user_by_id_grpc(cls, user_id: int):
        with grpc.insecure_channel(
                f'{os.environ["GRPC_USERSERVICE_HOST"]}:{os.environ["GRPC_USERSERVICE_PORT"]}') as channel:
            stub = proto.userService_pb2_grpc.UserServiceStub(channel)
            request = proto.userService_pb2.UserRequest(id=user_id)
            response = stub.getUserById(request)
            return response
