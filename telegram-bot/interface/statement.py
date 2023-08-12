import json
from dataclasses import dataclass


@dataclass
class Statement:
    def __init__(self, name, surname, patronymic, group):
        self.name = name
        self.surname = surname
        self.patronymic = patronymic
        self.group = group

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True,
                          indent=4)

    @staticmethod
    def fromJSON(body):
        json_body = json.loads(body)
        return Statement(name=json_body['name'],
                         surname=json_body['surname'],
                         patronymic=json_body['patronymic'],
                         group=json_body['group'])
