from datetime import datetime
from typing import Optional


class Notification:
    def __init__(self, user_id, chat_id, text, title, send_time, id: int = None) -> None:
        self.id = id
        self.user_id = user_id
        self.chat_id = chat_id
        self.text = text
        self.title = title
        self.send_time = send_time

    id: Optional[int]
    user_id: int
    chat_id: int
    text: str
    title: str
    send_time: datetime

    def toJSON(self):
        return f'{{"id": {self.id}, ' \
               f'"user_id": {self.user_id}, ' \
               f'"chat_id": {self.chat_id}, ' \
               f'"text": "{self.text}", ' \
               f'"title": "{self.title}", ' \
               f'"send_time": {str(int(self.send_time.timestamp()))}}}'
