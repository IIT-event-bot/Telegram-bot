import asyncio
import json
import logging
from datetime import datetime, timedelta

from models.notification import Notification
from models.notification_db import NotificationDB
from rabbit import rabbitmq as rabbit
from repository import notification_redis as redis_repository
from repository import notification_repository as repository

logger = logging.getLogger()


def check_event_time():
    logger.info('Check sending time nearest events')
    notifications = redis_repository.get_with_now_send_time_notifications()
    for notification in notifications:
        asyncio.run(rabbit.send_message(f'{{ "chat_id": {notification.chat_id}, '
                                        f'"text": "{notification.text}", '
                                        f'"title": "{notification.title}" }}'))


def check_event_on_next_hour():
    logger.info('Check events on next hour')
    saved_notifications = repository.get_notification_on_hour()
    saved_notifications.sort(key=lambda x: x.send_time, reverse=False)
    notifications = []
    for notification in saved_notifications:
        notifications.append(Notification(id=notification.id,
                                          user_id=notification.user_id,
                                          chat_id=notification.chat_id,
                                          text=notification.text,
                                          title=notification.title,
                                          send_time=notification.send_time))
    redis_repository.push_event_to_queue(notifications)


def save_notification(notification: Notification) -> None:
    db_model = NotificationDB(user_id=notification.user_id,
                              chat_id=notification.chat_id,
                              text=notification.text,
                              title=notification.title,
                              send_time=notification.send_time)
    repository.save_notification(notification=db_model)
    notification.id = 0
    now = datetime.now()
    in_an_hour = now + timedelta(hours=1)
    if notification.send_time <= in_an_hour:
        redis_repository.push_event_to_queue([notification])


def convert_json_to_notification(json_body) -> Notification:
    return json.loads(json_body, object_hook=lambda n: Notification(user_id=n['user_id'],
                                                                    chat_id=n['chat_id'],
                                                                    text=n['text'],
                                                                    title=n['title'],
                                                                    send_time=datetime.fromisoformat(n['send_time'])))
