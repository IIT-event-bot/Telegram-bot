FROM python:3.9
WORKDIR /telegram-bot
RUN pip install pipenv
RUN pip install python-dotenv
RUN pip install aioamqp
RUN pip install aiogram
COPY Pipfile.lock Pipfile.lock
RUN pipenv sync
RUN python -m grpc_tools.protoc -I proto --python_out=proto --grpc_python_out=proto proto/userService.proto
COPY . /telegram-bot
CMD ["python", "main.py"]