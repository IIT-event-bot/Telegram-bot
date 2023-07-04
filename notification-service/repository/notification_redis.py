import json
from datetime import datetime

import redis
from models.notification import Notification

repository = redis.Redis(host='localhost', port=6379, db=0)
QUEUE_NAME = 'notifications'


def push_event_to_queue(notifications: list[Notification]) -> None:
    saved = repository.lrange(QUEUE_NAME, start=0, end=-1)
    if len(saved) != 0:
        clear_saved_notification()
    json_list = []
    for notification in saved:
        n_json = json.loads(notification)
        json_list.append(n_json)

    for notification in notifications:
        n_json_bytes = bytes(notification.toJSON().encode('utf-8'))
        if n_json_bytes in saved:
            continue
        json_list.append(json.loads(n_json_bytes))

    json_list.sort(key=lambda x: x['send_time'], reverse=False)

    for notification in json_list:
        obj = json.dumps(notification)
        repository.lpush(QUEUE_NAME, obj)


def clear_saved_notification():
    while True:
        if repository.rpop(QUEUE_NAME) is None:
            break


def get_with_now_send_time_notifications() -> list[Notification]:
    now = datetime.now()
    notifications = []
    while True:
        json_notification = repository.rpop(QUEUE_NAME)
        if json_notification is None:
            break
        notification = json.loads(json_notification)
        notification_time = datetime.fromtimestamp(notification['send_time'])
        if notification_time.minute == now.minute:
            notifications.append(Notification(id=notification['id'],
                                              user_id=notification['user_id'],
                                              chat_id=notification['chat_id'],
                                              text=notification['text'],
                                              title=notification['title'],
                                              send_time=notification['send_time']))
        else:
            repository.rpush(QUEUE_NAME, json_notification)
            break
    return notifications
