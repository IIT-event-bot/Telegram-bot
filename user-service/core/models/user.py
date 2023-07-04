from dataclasses import dataclass

from sqlalchemy import Column, INTEGER, VARCHAR, ForeignKey

from ..repositories.db.base import Base


@dataclass
class User(Base):
    __tablename__ = 'users'

    id = Column(INTEGER, primary_key=True, autoincrement=True)
    chat_id = Column(INTEGER, unique=True, nullable=False)
    username = Column(VARCHAR(35))
    role_id = Column(INTEGER, ForeignKey('roles.id'))
