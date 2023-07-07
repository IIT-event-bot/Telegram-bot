FROM python:3.9
WORKDIR /telegram-bot
RUN pip install pipenv
RUN pip install python-dotenv
RUN pip install aioamqp
RUN pip install aiogram
COPY Pipfile.lock Pipfile.lock
RUN pipenv sync
COPY . /telegram-bot
CMD ["python", "main.py"]