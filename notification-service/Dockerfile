FROM python:3.9
WORKDIR /notification-service
RUN pip install pipenv
RUN pip install python-dotenv
RUN pip install sqlalchemy
RUN pip install psycopg2-binary
RUN pip install aioamqp
RUN pip install apscheduler
RUN pip install redis
COPY Pipfile.lock Pipfile.lock
RUN pipenv sync
COPY . /notification-service
CMD ["python", "main.py"]