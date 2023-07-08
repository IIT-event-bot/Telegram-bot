import logging

from aiogram.types import Message, InlineKeyboardButton, InlineKeyboardMarkup, CallbackQuery

from interface.Statement import Statement

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
    logger.info(f'user id: {message.from_user.id} /start')


def start_inline_keyboard() -> InlineKeyboardMarkup:
    create_statement_btn = InlineKeyboardButton('Подать заявку на добавление 🗎', callback_data='create_statement')
    help_btn = InlineKeyboardButton('Помощь', callback_data='help')
    return InlineKeyboardMarkup(row_width=2).add(create_statement_btn, help_btn)


async def help_message(message: Message):
    """сообщение о функциях команд /help"""
    await message.answer(f'Чем могу помочь?', reply_markup=help_inline_keyboard())
    logger.info(f'user id: {message.from_user.id} /help')


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


def confirmation_inline_keyboard() -> InlineKeyboardMarkup:
    confirmation_btn = InlineKeyboardButton(f'Уведомление о событии получено {Icon.CHECK.value}',
                                            callback_data='check')
    return InlineKeyboardMarkup().add(confirmation_btn)


def mark_inline_keyboard(event_id) -> InlineKeyboardMarkup:
    one_star_btn = InlineKeyboardButton(Icon.STAR.value, callback_data=f'{event_id}:mark:1')
    two_star_btn = InlineKeyboardButton(Icon.TWO_STAR.value, callback_data=f'{event_id}:mark:2')
    three_star_btn = InlineKeyboardButton(Icon.THREE_STAR.value, callback_data=f'{event_id}:mark:3')
    four_star_btn = InlineKeyboardButton(Icon.FOUR_STAR.value, callback_data=f'{event_id}:mark:4')
    five_star_btn = InlineKeyboardButton(Icon.FIVE_STAR.value, callback_data=f'{event_id}:mark:5')
    return InlineKeyboardMarkup(row_width=2).add(one_star_btn, two_star_btn, three_star_btn, four_star_btn,
                                                 five_star_btn)


def comment_inline_keyboard() -> InlineKeyboardMarkup:
    write_comment_btn = InlineKeyboardButton('Оставить комментарий', callback_data='add_comment')
    cancel_btn = InlineKeyboardButton('Нет', callback_data='cancel_comment')
    return InlineKeyboardMarkup(row_width=2).add(write_comment_btn, cancel_btn)


def student_main_inline_keyboard() -> InlineKeyboardMarkup:
    get_schedule = InlineKeyboardButton('Получить расписание', callback_data='get_schedule')
    notification_settings = InlineKeyboardButton('Настройка уведомлений', callback_data='notification_settings')
    return InlineKeyboardMarkup(row_width=2).add(get_schedule, notification_settings)


async def add_statement(message: Message, state: FSMContext):
    """парсинг заявки"""
    try:
        split = message.text.split()
        name = split[0]
        surname = split[1]
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
    # back_btn = InlineKeyboardButton('Назад', callback_data='cancel_statement')
    return InlineKeyboardMarkup(row_width=2).add(create_statement_btn)


async def callback_query_send_statement(call: CallbackQuery, state: FSMContext):
    """отправка заявки"""
    await call.message.edit_text(text='Заявка успешно отправлена! ' + Icon.CHECK.value)
    data = await state.get_data('user_data')
    statement: Statement = data['user_data']
    # logger.info(f'user id: {call.from_user.id} отправили заявку')
    await rabbit.send_message_to_user_service(
        f'{{"method": "ADD_STATEMENT", "body": {{ "id": {call.message.chat.id}, "name": "{statement.name}", "surname": "{statement.surname}", "patronymic": "{statement.patronymic}", "groupName": "{statement.group}" }} }}')
    # await state.finish()


def send_comment_inline_keyboard() -> InlineKeyboardMarkup:
    send = InlineKeyboardButton('Отправить комментарий ➡', callback_data='send_comment')
    cancel_sending = InlineKeyboardButton('Отмена ❌', callback_data='cancel_comment')
    return InlineKeyboardMarkup(row_width=2).add(send, cancel_sending)


async def add_comment(call: CallbackQuery, state):
    await call.message.edit_text('Отправьте комментарий на событие')
    await States.comment.set()


async def parse_comment(message: Message, state: FSMContext):
    await message.edit_text(f'Комментарий: {message.text}\n\nОтправить этот комментарий?',
                            reply_markup=send_comment_inline_keyboard())
    comment = message.text
    logger.info(f'user id: {message.from_user.id} спарсили комментарий')


async def callback_query_help(call: CallbackQuery):
    await help_message(call.message)
    logger.info(f'user id: {call.from_user.id} /help')


async def callback_query_mark(call: CallbackQuery):
    await call.message.edit_text(
        text='Спасибо за оценку! Хочешь оставить комментарий? Напиши свой отзыв и нажми "Отправить комментарий"',
        reply_markup=comment_inline_keyboard())

    split = call.data.split(':')
    grade = int(split[-1])
    event_id = int(split[0])
    await rabbit.send_message_to_event_service(f'{{ "method": "ADD_GRADE", '
                                               f'"body": {{ '
                                               f'"event_id": {event_id}, '
                                               f'"grade": {grade} '
                                               f'}}'
                                               f'}}')


async def callback_query_send_comment(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text(text='Комментарий успешно отправлен! ' + Icon.CHECK.value,
                                 # reply_markup=student_main_inline_keyboard()
                                 )
    # await state.finish()
    await States.comment.set()
    logger.info(f'user id: {call.from_user.id} отправили комментарий')


async def callback_query_cancel_comment(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text(text='Отказ от комментария',
                                 # reply_markup=student_main_inline_keyboard()
                                 )
    await state.finish()
    logger.info(f'user id: {call.from_user.id} отказался от комментария')


async def callback_query_confirmation_of_notification(call: CallbackQuery):
    logger.info(f'user id: {call.from_user.id} подтвердил получение уведомления')


async def callback_query_cancel_statement(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text(
        text=f'Привет, я бот института информационных технологий. Я помогу тебе узнать свое расписание и '
             f'буду сообщать тебе о главных мероприятиях института. Нажми "Подать заявку на добавление" и'
             f' заполни форму.', reply_markup=start_inline_keyboard())
    await state.finish()
    logger.info(f'user id: {call.from_user.id} /назад')


async def callback_query_confirmation_of_notification(call: CallbackQuery):
    event_id = call.data[13:]
