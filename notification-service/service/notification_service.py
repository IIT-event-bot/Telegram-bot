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
        asyncio.run(rabbit.send_message(f'{{ "chat_id": {notification.chat_id},'
                                        f'"type": "{notification.type}", '
                                        f'"text": "{notification.text}", '
                                        f'"title": "{notification.title}" }}'))


def check_event_on_next_hour():
    logger.info('Check events on next hour')
    saved_notifications = repository.get_notification_on_hour()
    # saved_notifications.sort(key=lambda x: x.send_time, reverse=False)
    notifications = []
    for saved_notification in saved_notifications:
        notification = Notification()
        notification.id = saved_notification.id,
        for n in notification.id:
            notification.id = n
        notification.send_time = saved_notification.send_time
        notification.type = saved_notification.type
        notification.chat_id = saved_notification.chat_id
        notification.text = saved_notification.text
        notification.title = saved_notification.title

        notifications.append(notification)
    redis_repository.push_event_to_queue(notifications)


async def save_notification(notification: Notification) -> None:
    if notification.type == 'INFO':
        await rabbit.send_message(f'{{ "chat_id": {notification.chat_id}, '
                                  f'"type": "{notification.type}", '
                                  f'"text": "{notification.text}", '
                                  f'"title": "{notification.title}" }}')
    elif notification.type == 'EVENT' or notification.type == 'FEEDBACK':
        now = datetime.now()
        in_an_hour = now + timedelta(hours=1)
        if notification.send_time <= in_an_hour:
            notification.id = 0
            redis_repository.push_event_to_queue([notification])
    else:
        return
    save_notification_to_db(notification)


def save_notification_to_db(notification: Notification) -> None:
    db_model = NotificationDB(type=notification.type,
                              chat_id=notification.chat_id,
                              text=notification.text,
                              title=notification.title,
                              send_time=notification.send_time)
    repository.save_notification(notification=db_model)


def convert_json_to_notification(json_body) -> Notification:
    json_notification = json.loads(json.loads(json_body))

    notification = Notification()
    notification.type = json_notification['type']
    notification.chat_id = int(json_notification['chatId'])
    notification.text = json_notification['text']
    notification.title = json_notification['title']
    if json_notification.get('send_time') is not None and notification.type != 'INFO':
        notification.send_time = datetime.fromisoformat(json_notification['send_time'])
    elif notification.type == 'INFO':
        notification.send_time = datetime.now()
    else:
        raise Exception('Notification with type not INFO must be with send_time parameter')

    return notification
