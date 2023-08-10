import json


class Student:

    def __init__(self, student_id: int,
                 user_id: int,
                 name: str,
                 surname: str,
                 patronymic: str,
                 group_name: str) -> None:
        self.student_id = student_id
        self.user_id = user_id
        self.name = name
        self.surname = surname
        self.patronymic = patronymic
        self.group_name = group_name

    student_id: int
    user_id: int
    name: str
    surname: str
    patronymic: str
    group_name: str

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)
