from sqlalchemy import Column, INTEGER, VARCHAR, TIMESTAMP

from repository.db.base import Base


class NotificationDB(Base):
    __tablename__ = 'notifications'

    id = Column(INTEGER, autoincrement=True, primary_key=True)
    type = Column(VARCHAR)
    chat_id = Column(INTEGER)
    text = Column(VARCHAR)
    title = Column(VARCHAR)
    send_time = Column(TIMESTAMP)
