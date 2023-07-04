import logging
from abc import ABC, abstractmethod

import os

from core.models.group import Group
from core.models.manager import Manager
from core.models.role import Role
from core.models.statement import Statement
from core.models.student import Student
from core.models.user import User
from dotenv import dotenv_values
from sqlalchemy import create_engine
from sqlalchemy.engine import Engine
from sqlalchemy.orm import Session, sessionmaker, scoped_session

from .base import Base

config = dotenv_values()
logger = logging.getLogger()


class DBConnection(ABC):
    def __init__(self) -> None:
        self.__base = Base

    @abstractmethod
    def session(self) -> Session:
        pass

    @abstractmethod
    def _get_engine(self) -> Engine:
        pass

    def _create_db(self, engine: Engine) -> None:
        Group()
        User()
        Role()
        Student()
        Statement()
        Manager()
        self.__base.metadata.create_all(engine)


class PostgresConnection(DBConnection):
    def __init__(self) -> None:
        super().__init__()
        self.__session = self.__get_db_connection()

    def _get_engine(self) -> Engine:
        try:
            # db_url = f'postgresql+psycopg2://{os.environ["DB_USER_NAME"]}:{os.environ["DB_USER_PASSWORD"]}@{os.environ["DB_HOST"]}:{os.environ["DB_PORT"]}/{os.environ["DB_NAME"]}'
            db_url = f'postgresql+psycopg2://{config["DB_USER_NAME"]}:{config["DB_USER_PASSWORD"]}@{config["DB_HOST"]}:{config["DB_PORT"]}/{config["DB_NAME"]}'
            engine = create_engine(db_url, echo=False)
            engine.connect()

            return engine
        except Exception:
            logger.error('Failed to connect to the database')
            raise ConnectionError

    def __get_db_connection(self) -> Session:
        engine = self._get_engine()
        self._create_db(engine=engine)
        Session = scoped_session(sessionmaker(bind=engine,
                                              autocommit=False,
                                              autoflush=False))
        return Session()

    def session(self):
        return self.__session


postgres_connection = PostgresConnection()


def get_postgres_connection() -> PostgresConnection:
    return postgres_connection
