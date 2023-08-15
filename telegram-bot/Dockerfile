FROM python:3.9
WORKDIR /telegram-bot
RUN pip install pipenv
RUN pip install python-dotenv
RUN pip install aioamqp
RUN pip install aiogram
RUN pip install grpcio-tools
RUN pip install redis
COPY Pipfile.lock Pipfile.lock
RUN pipenv sync
#RUN python -m grpc_tools.protoc -I proto --python_out=grpc_service --grpc_python_out=grpc_service proto/groupService.proto
COPY . /telegram-bot
CMD ["python", "main.py"]