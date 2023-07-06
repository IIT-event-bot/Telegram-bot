from concurrent import futures

import user_service_pb2
import user_service_pb2_grpc
from ..deps import ComponentsContainer

ioc = ComponentsContainer()
user_service = ioc.user_service


class UserServiceServer(user_service_pb2_grpc.UserServiceServicer):

    def getUser(self, request, context):
        id = request.id
        user = user_service.get_user_by_id(id)
        response = user_service_pb2.UserRequest()
        response.id = user.id
        response.chat_id = user.chat_id
        response.username = user.username
        return response

    def getUsersByGroup(self, request, context):
        return super().getUsersByGroup(request, context)


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    user_service_pb2_grpc.add_UserServiceServicer_to_server(UserServiceServer(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    server.wait_for_termination()
