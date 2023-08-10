import json


class User:
    def __init__(self, user_id, username, role) -> None:
        self.user_id = user_id
        self.username = username
        self.role = role

    user_id: int
    username: str
    role: str

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)
