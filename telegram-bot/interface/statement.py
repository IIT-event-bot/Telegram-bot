from dataclasses import dataclass

@dataclass
class Statement:
    def __init__(self, name, surname, patronymic, group_name):
        self.name = name
        self.surname = surname
        self.patronymic = patronymic
        self.group = group_name