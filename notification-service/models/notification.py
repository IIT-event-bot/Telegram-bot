from datetime import datetime
from typing import Optional


class Notification:
    id: Optional[int]
    type: str
    chat_id: int
    text: str
    title: str
    send_time: Optional[datetime]
    event_id: int

    def toJSON(self):
        return f'{{"id": {self.id}, ' \
               f'"type": "{self.type}", ' \
               f'"chat_id": {self.chat_id}, ' \
               f'"text": "{self.text}", ' \
               f'"title": "{self.title}",' \
               f'"event_id": {self.event_id} ' \
               f'"send_time": {str(int(self.send_time.timestamp()))}}}'
