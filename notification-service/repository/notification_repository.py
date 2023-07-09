from datetime import datetime, timedelta

import pytz

from models.notification_db import NotificationDB
from sqlalchemy import and_

from .db.connections import DBConnection, PostgresConnection

connection: DBConnection = PostgresConnection()
session = connection.session()


def save_notification(notification: NotificationDB) -> None:
    session.add(notification)
    session.commit()


def get_notification_on_hour() -> list[NotificationDB]:
    now = datetime.utcnow() + timedelta(hours=5)
    in_an_hour = now + timedelta(hours=1)
    notifications = session.query(NotificationDB).filter(
        and_(NotificationDB.send_time <= in_an_hour, NotificationDB.send_time >= now)).all()
    session.commit()
    return notifications
