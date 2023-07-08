from aiogram.dispatcher.filters.state import StatesGroup, State


class States(StatesGroup):
    add_statement = State()
    comment = State()
    send_statement = State()
