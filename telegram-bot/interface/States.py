from aiogram.dispatcher.filters.state import StatesGroup, State

class States(StatesGroup):

    statement = State()
    comment = State()
    finish = State()