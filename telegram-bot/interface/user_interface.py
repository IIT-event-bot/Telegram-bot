import logging

from aiogram.types import Message, InlineKeyboardButton, InlineKeyboardMarkup, CallbackQuery

from interface.statement import Statement

logger = logging.getLogger()
from aiogram.dispatcher import FSMContext

from rabbit.rabbitmq import instance_rabbit_client as rabbit
from interface.Icon import *
from interface.States import *

import re


async def start_message(message: Message):
    """стартовое сообщение"""
    await message.answer(f'Привет, я бот института информационных технологий. Я помогу тебе узнать свое расписание и '
                         f'буду сообщать тебе о главных мероприятиях института. Нажми "Подать заявку на добавление" и'
                         f' заполни форму.', reply_markup=start_inline_keyboard())
    """Отправка сообщение о добавлении нового пользователя"""
    await rabbit.send_message_to_user_service(
        f'{{"method": "ADD_USER", "body": {{ "username": "{message.chat.username}", "id": {message.chat.id} }} }}')
    # logger.info(f'user id: {message.from_user.id} /start')


def start_inline_keyboard() -> InlineKeyboardMarkup:
    create_statement_btn = InlineKeyboardButton('Подать заявку на добавление 🗎', callback_data='create_statement')
    help_btn = InlineKeyboardButton('Помощь', callback_data='help')
    return InlineKeyboardMarkup(row_width=2).add(create_statement_btn, help_btn)


async def help_message(message: Message):
    """сообщение о функциях команд /help"""
    await message.answer(f'Чем могу помочь?', reply_markup=help_inline_keyboard())
    # logger.info(f'user id: {message.from_user.id} /help')


async def callback_query_help(call: CallbackQuery):
    await help_message(call.message)
    # logger.info(f'user id: {call.from_user.id} /help')


def help_inline_keyboard() -> InlineKeyboardMarkup:
    create_statement_btn = InlineKeyboardButton('Подать заявку на добавление', callback_data='create_statement')
    iit_contacts = InlineKeyboardButton('Контакты ИИТ', url='https://iit.csu.ru/')
    return InlineKeyboardMarkup(row_width=2).add(create_statement_btn, iit_contacts)


async def callback_query_statement(call: CallbackQuery, state: FSMContext):
    """подача заявки на добавление"""
    await call.message.edit_text(
        text='Отправь мне свое ФИО и группу в формате: "Фамилия Имя Отчество группа".\nПример: "Иванов Иван '
             'Иванович ПрИ-200"\nПосле того как отправишь анкету нажми "Отправить заявку"')
    await States.add_statement.set()
    await state.set_data({'message_id': call.message.message_id})


def confirmation_inline_keyboard(event_id: int) -> InlineKeyboardMarkup:
    confirmation_btn = InlineKeyboardButton(f'Уведомление о событии получено {Icon.CHECK.value}',
                                            callback_data=f'check:{event_id}')
    return InlineKeyboardMarkup().add(confirmation_btn)


# def student_main_inline_keyboard() -> InlineKeyboardMarkup:
#     get_schedule = InlineKeyboardButton('Получить расписание', callback_data='get_schedule')
#     notification_settings = InlineKeyboardButton('Настройка уведомлений', callback_data='notification_settings')
#     return InlineKeyboardMarkup(row_width=2).add(get_schedule, notification_settings)


async def add_statement(message: Message, state: FSMContext):
    """парсинг заявки"""
    try:
        split = message.text.split()
        name = split[1]
        surname = split[0]
        patronymic = split[2]
        group_name = split[3]
        if not re.match(pattern='[а-яА-Я]{3}-\d{3}', string=group_name):
            raise Exception(f'Группа заполнена неправильно: {group_name}')
    except Exception as e:
        logger.error(e)
        await message.bot.send_message(message.chat.id, 'Заявка заполнена неправильно, заполни, по форме!\n'
                                                        'Пример: "Иванов Иван Иванович ПрИ-201"')
        return
    data: dict = await state.get_data('message_id')
    await message.bot.edit_message_text(
        text=f'Заявка на добавление:\n\nФамилия: {surname}\nИмя: {name}\nОтчество: {patronymic}\nгруппа: {group_name}',
        chat_id=message.chat.id,
        message_id=data['message_id'],
        reply_markup=create_statement_inline_keyboard())
    await States.send_statement.set()
    await state.finish()
    await state.set_data(
        {'user_data': Statement(name=name, surname=surname, patronymic=patronymic, group_name=group_name)})


def create_statement_inline_keyboard() -> InlineKeyboardMarkup:
    create_statement_btn = InlineKeyboardButton('Отправить заявку ➡', callback_data='send_statement')
    cancel_statement = InlineKeyboardButton('Отмена ❌', callback_data='cancel_statement')
    return InlineKeyboardMarkup(row_width=1).add(create_statement_btn, cancel_statement)


async def callback_query_send_statement(call: CallbackQuery, state: FSMContext):
    """отправка заявки"""
    await call.message.edit_text(text='Заявка успешно отправлена! ' + Icon.CHECK.value)
    data = await state.get_data('user_data')
    statement: Statement = data['user_data']
    # logger.info(f'user id: {call.from_user.id} отправили заявку')
    await rabbit.send_message_to_user_service(
        f'{{"method": "ADD_STATEMENT", "body": {{ "id": {call.message.chat.id}, "name": "{statement.name}", "surname": "{statement.surname}", "patronymic": "{statement.patronymic}", "groupName": "{statement.group}" }} }}')
    # await state.finish()


async def callback_query_mark(call: CallbackQuery):
    """отправка сообщения об обратной связи"""
    split = call.data.split(':')
    grade = int(split[-1])
    event_id = int(split[0])
    await call.message.edit_text(
        text='Спасибо за оценку! Хочешь оставить комментарий? Напиши свой отзыв о событии',
        reply_markup=comment_inline_keyboard(event_id))
    await rabbit.send_message_to_event_service(f'{{ "method": "ADD_GRADE", '
                                               f'"body": {{ '
                                               f'"event_id": {event_id}, '
                                               f'"grade": {grade} '
                                               f'}}'
                                               f'}}')


def mark_inline_keyboard(event_id) -> InlineKeyboardMarkup:
    """кнопки для оценки события"""
    one_star_btn = InlineKeyboardButton(Icon.STAR.value, callback_data=f'{event_id}:mark:1')
    two_star_btn = InlineKeyboardButton(Icon.TWO_STAR.value, callback_data=f'{event_id}:mark:2')
    three_star_btn = InlineKeyboardButton(Icon.THREE_STAR.value, callback_data=f'{event_id}:mark:3')
    four_star_btn = InlineKeyboardButton(Icon.FOUR_STAR.value, callback_data=f'{event_id}:mark:4')
    five_star_btn = InlineKeyboardButton(Icon.FIVE_STAR.value, callback_data=f'{event_id}:mark:5')
    return InlineKeyboardMarkup(row_width=2).add(one_star_btn, two_star_btn, three_star_btn, four_star_btn,
                                                 five_star_btn)


def comment_inline_keyboard(event_id: int) -> InlineKeyboardMarkup:
    write_comment_btn = InlineKeyboardButton('Оставить коммент.', callback_data=f'add_comment:{event_id}')
    cancel_btn = InlineKeyboardButton('Не оставлять', callback_data='cancel_comment')
    return InlineKeyboardMarkup(row_width=2).add(write_comment_btn, cancel_btn)


async def add_comment(call: CallbackQuery, state: FSMContext):
    event_id = int(call.data.split(':')[-1])
    await call.message.edit_text('Отправьте комментарий на событие ✍')
    await state.set_data({'event_id': event_id, 'message_id': call.message.message_id})
    await States.comment.set()


async def callback_query_edit_comment(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text('Напиши новый комментарий')
    States.edit_comment.set()
    message_id = call.message.message_id
    await state.set_data({'message_id': message_id})


async def edit_comment(message: Message, state: FSMContext):
    data = await state.get_data('message_id')
    message_id = data['message_id']
    await message.bot.edit_message_text(text=message.text, chat_id=message.chat.id,
                                        message_id=message_id, reply_markup=send_comment_inline_keyboard())


async def parse_comment(message: Message, state: FSMContext):
    message_id: dict = await state.get_data('message_id')
    event_id: dict = await state.get_data('event_id')
    await message.bot.edit_message_text(text=f'Комментарий: {message.text}\n\nОтправить этот комментарий?',
                                        message_id=message_id['message_id'],
                                        chat_id=message.chat.id,
                                        reply_markup=send_comment_inline_keyboard())
    # await state.set_state(States.comment)
    await state.finish()
    await state.set_data({'event_id': event_id['event_id'], 'message_text': message.text})


def send_comment_inline_keyboard() -> InlineKeyboardMarkup:
    send = InlineKeyboardButton('Отправить комментарий ➡', callback_data='send_comment')
    cancel_sending = InlineKeyboardButton('Отмена ❌', callback_data='cancel_comment')
    return InlineKeyboardMarkup(row_width=2).add(send, cancel_sending)


async def callback_query_cancel_comment(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text(text='Отказ от комментария')
    await state.finish()
    # logger.info(f'user id: {call.from_user.id} отказался от комментария')


async def callback_query_send_comment(call: CallbackQuery, state: FSMContext):
    data = await state.get_data('event_id')
    event_id = data['event_id']
    message_text = data['message_text']
    await call.message.edit_text(text='Комментарий успешно отправлен! ' + Icon.CHECK.value)
    await state.finish()
    await rabbit.send_message_to_event_service(
        f'{{ "method": "ADD_FEEDBACK", "body": {{ "event_id": {event_id}, "text": "{message_text}" }} }}')


async def callback_query_cancel_statement(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text(
        text=f'Привет, я бот института информационных технологий. Я помогу тебе узнать свое расписание и '
             f'буду сообщать тебе о главных мероприятиях института. Нажми "Подать заявку на добавление" и'
             f' заполни форму.', reply_markup=start_inline_keyboard())
    await state.finish()
    # logger.info(f'user id: {call.from_user.id} /назад')


async def callback_query_check_notification(call: CallbackQuery):
    event_id = call.data.split(':')[-1]
    await call.message.edit_text(call.message.text)
    await rabbit.send_message_to_event_service(
        f'{{ "method": "CHECK_EVENT", "body": {{ "event_id": {event_id}, "user_id": "{call.from_user.id}" }} }}')
