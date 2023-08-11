import json


class Student:
    def __init__(self, student_id: int,
                 user_id: int,
                 name: str,
                 surname: str,
                 patronymic: str,
                 group_name: str,
                 group_id: int) -> None:
        self.student_id = student_id
        self.user_id = user_id
        self.name = name
        self.surname = surname
        self.patronymic = patronymic
        self.group_name = group_name
        self.group_id = group_id

    student_id: int
    user_id: int
    name: str
    surname: str
    patronymic: str
    group_name: str
    group_id: int

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True,
                          indent=4)

    @staticmethod
    def fromJSON(body):
        student_json = json.loads(body)
        return Student(student_id=student_json['student_id'],
                       user_id=student_json['user_id'],
                       name=student_json['name'],
                       surname=student_json['surname'],
                       patronymic=student_json['patronymic'],
                       group_name=student_json['group_name'],
                       group_id=student_json['group_id'])
