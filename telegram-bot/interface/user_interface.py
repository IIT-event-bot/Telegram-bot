import datetime
import logging
import os

import redis
from aiogram.types import Message, InlineKeyboardButton, InlineKeyboardMarkup, CallbackQuery

from interface.statement import Statement
from model.lesson import Lesson
from repositories.schedule_repository.redis_schedule_repository import RedisScheduleCacheRepository
from repositories.user_repository.redis_repository import RedisUserCacheRepository
from services.group_service.grpc_group_service import GrpcGroupService
from services.schedule_service.grpc_schedule_service import GrpcScheduleService
from services.schedule_service.proxy_cache_schedule_service import ProxyCacheScheduleRepository
from services.schedule_service.schedule_service import ScheduleService
from services.user_service.grpc_user_service import GrpcUserService
from services.user_service.proxy_cached_user_service import ProxyCachedUserService
from services.user_service.user_service import UserService

logger = logging.getLogger()
from aiogram.dispatcher import FSMContext

from rabbit.rabbitmq import instance_rabbit_client as rabbit
from interface.Icon import *
from interface.States import *

import re

user_service: UserService = ProxyCachedUserService(user_service=GrpcUserService(GrpcGroupService()),
                                                   repository=RedisUserCacheRepository(
                                                       redis.Redis(host=os.environ['REDIS_HOST'],
                                                                   port=int(os.environ['REDIS_PORT']),
                                                                   db=0)))

schedule_service: ScheduleService = ProxyCacheScheduleRepository(GrpcScheduleService(),
                                                                 RedisScheduleCacheRepository(
                                                                     redis.Redis(host=os.environ['REDIS_HOST'],
                                                                                 port=int(os.environ['REDIS_PORT']),
                                                                                 db=0)
                                                                 ))


async def start_message(message: Message):
    """стартовое сообщение"""
    try:
        if user_service.is_student(message.chat.id):
            await message.answer(f'Чем могу помочь?', reply_markup=student_button())
        else:
            await message.answer(
                f'Привет, я бот института информационных технологий. Я помогу тебе узнать свое расписание и '
                f'буду сообщать тебе о главных мероприятиях института. Нажми "Подать заявку на добавление" и'
                f' заполни форму.', reply_markup=start_inline_keyboard())
            """Отправка сообщение о добавлении нового пользователя"""
            await rabbit.send_message_to_user_service(
                f'{{"method": "ADD_USER", "body": {{ "username": "{message.chat.username}", "id": {message.chat.id} }} }}')
    except Exception as e:
        logger.error(e)
        await message.answer('Произошла неизвестная ошибка, попробуйте позже')


# logger.info(f'user id: {message.from_user.id} /start')


def start_inline_keyboard() -> InlineKeyboardMarkup:
    create_statement_btn = InlineKeyboardButton('Подать заявку на добавление 🗎', callback_data='create_statement')
    help_btn = InlineKeyboardButton('Помощь', callback_data='help')
    return InlineKeyboardMarkup(row_width=2).add(create_statement_btn, help_btn)


async def help_message(message: Message):
    """сообщение о функциях команд /help"""
    try:
        if user_service.is_student(message.chat.id):
            await message.answer(f'Чем могу помочь?', reply_markup=student_button())
        else:
            await message.answer(f'Чем могу помочь?', reply_markup=help_inline_keyboard())
    except Exception as e:
        logger.error(e)
        await message.answer('Произошла неизвестная ошибка, попробуйте позже')
    # logger.info(f'user id: {message.from_user.id} /help')


async def callback_query_help(call: CallbackQuery):
    await help_message(call.message)
    # logger.info(f'user id: {call.from_user.id} /help')


def help_inline_keyboard() -> InlineKeyboardMarkup:
    create_statement_btn = InlineKeyboardButton('Подать заявку на добавление', callback_data='create_statement')
    iit_contacts = InlineKeyboardButton('Контакты ИИТ', url='https://iit.csu.ru/')
    return InlineKeyboardMarkup(row_width=2).add(create_statement_btn, iit_contacts)


def cancel_statement_inline_keyboard() -> InlineKeyboardMarkup:
    cancel_statement = InlineKeyboardButton('Отмена ❌', callback_data='cancel_statement')
    return InlineKeyboardMarkup().add(cancel_statement)


async def callback_query_statement(call: CallbackQuery, state: FSMContext):
    """подача заявки на добавление"""
    await call.message.edit_text(
        text='Отправь мне свое ФИО и группу в формате: "Фамилия Имя Отчество группа".\nПример: "Иванов Иван '
             'Иванович ПрИ-200"\nПосле того как отправишь анкету нажми "Отправить заявку"')
    await call.message.edit_reply_markup(reply_markup=cancel_statement_inline_keyboard())
    await States.add_statement.set()
    await state.set_data({'message_id': call.message.message_id})


def confirmation_inline_keyboard(event_id: int) -> InlineKeyboardMarkup:
    confirmation_btn = InlineKeyboardButton(f'Уведомление о событии получено {Icon.CHECK.value}',
                                            callback_data=f'check:{event_id}')
    return InlineKeyboardMarkup().add(confirmation_btn)


async def add_statement(message: Message, state: FSMContext):
    """парсинг заявки"""
    try:
        split = message.text.split()
        name = split[1]
        surname = split[0]
        patronymic = split[2]
        group_name = split[3]
        if not re.match(pattern='[а-яА-Я]{2,3}-\d{3}', string=group_name):
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
        {'user_data': Statement(name=name, surname=surname, patronymic=patronymic, group=group_name).toJSON()})


def create_statement_inline_keyboard() -> InlineKeyboardMarkup:
    create_statement_btn = InlineKeyboardButton('Отправить заявку ➡', callback_data='send_statement')
    cancel_statement = InlineKeyboardButton('Отмена ❌', callback_data='cancel_statement')
    return InlineKeyboardMarkup(row_width=1).add(create_statement_btn, cancel_statement)


async def callback_query_send_statement(call: CallbackQuery, state: FSMContext):
    """отправка заявки"""
    await call.message.edit_text(text='Заявка успешно отправлена! ' + Icon.CHECK.value)
    data = await state.get_data('user_data')
    statement: Statement = Statement.fromJSON(data['user_data'])
    # logger.info(f'user id: {call.from_user.id} отправили заявку')
    await rabbit.send_message_to_user_service(
        f'{{"method": "ADD_STATEMENT", "body": '
        f'{{ "id": {call.message.chat.id}, '
        f'"name": "{statement.name}", '
        f'"surname": "{statement.surname}", '
        f'"patronymic": "{statement.patronymic}", '
        f'"groupName": "{statement.group}" }} '
        f'}}')
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


def student_button() -> InlineKeyboardMarkup:
    schedule_btn = InlineKeyboardButton('Расписание', callback_data='get_schedule')
    return InlineKeyboardMarkup(row_width=2).add(schedule_btn)


async def callback_get_schedule(call: CallbackQuery):
    await call.message.edit_text(text='Выберите тип расписания')
    await call.message.edit_reply_markup(reply_markup=InlineKeyboardMarkup(row_width=2).add(
        InlineKeyboardButton('Сегодня', callback_data='schedule_today'),
        InlineKeyboardButton('Завтра', callback_data='schedule_tomorrow'),
        InlineKeyboardButton('На неделю', callback_data='schedule_week')))


async def callback_get_schedule_today(call: CallbackQuery):
    try:
        student = user_service.get_student_by_user_id(user_id=call.message.chat.id)
        schedule = schedule_service.get_schedule_today(group_id=student.group_id)
        today_schedule_str = format_today_lesson_list(schedule)
        await call.bot.send_message(chat_id=call.message.chat.id,
                                    text=f'<b>Расписание на сегодня</b>\n\n{today_schedule_str}')
    except Exception as e:
        logger.error(e)
        await call.message.answer('Произошла неизвестная ошибка, попробуйте позже')


async def callback_get_schedule_tomorrow(call: CallbackQuery):
    try:
        student = user_service.get_student_by_user_id(user_id=call.message.chat.id)
        schedule = schedule_service.get_schedule_tomorrow(group_id=student.group_id)
        tomorrow_schedule_str = format_tomorrow_lesson_list(schedule)
        await call.bot.send_message(chat_id=call.message.chat.id,
                                    text=f'<b>Расписание на завтра</b>\n\n{tomorrow_schedule_str}')
    except Exception as e:
        logger.error(e)
        await call.message.answer('Произошла неизвестная ошибка, попробуйте позже')


async def callback_get_schedule_week(call: CallbackQuery):
    try:
        student = user_service.get_student_by_user_id(user_id=call.message.chat.id)
        schedule = schedule_service.get_schedule_on_week(group_id=student.group_id)
        week_schedule_str = format_week_lesson(schedule)
        await call.bot.send_message(chat_id=call.message.chat.id,
                                    text=f'<b>Расписание на неделю</b>{week_schedule_str}')
    except Exception as e:
        logger.error(e)
        await call.message.answer('Произошла неизвестная ошибка, попробуйте позже')


def format_week_lesson(schedule: dict[str, list[Lesson]]):
    lessons = str()
    for day in schedule:
        lessons += f'\n\n<b>[  <u>{format_day_name(day)}</u>  ]</b>\n'
        for lesson in schedule[day]:
            lessons += (f'<b>Название</b>: {lesson.title}\n'
                        f'<b>Аудитория</b>: {lesson.auditorium}\n'
                        f'<b>Преподаватель</b>: {lesson.teacher}\n'
                        f'<b>Начало пары</b>: {lesson.time_start.strftime("%H:%M")}\n'
                        f'<b>Конец пары</b>: {lesson.time_end.strftime("%H:%M")}')

    return lessons


def format_today_lesson_list(schedule: list[Lesson]):
    lessons = str()
    now = datetime.datetime.now().time()
    for lesson in schedule:
        if lesson.time_start <= now <= lesson.time_end:
            lessons += '<b>[  <u>Идет сейчас</u>  ]</b>\n'
        lessons += (f'<b>Название</b>: {lesson.title}\n'
                    f'<b>Аудитория</b>: {lesson.auditorium}\n'
                    f'<b>Преподаватель</b>: {lesson.teacher}\n'
                    f'<b>Начало пары</b>: {lesson.time_start.strftime("%H:%M")}\n'
                    f'<b>Конец пары</b>: {lesson.time_end.strftime("%H:%M")}\n')

    return lessons


def format_tomorrow_lesson_list(schedule: list[Lesson]):
    lessons = str()
    for lesson in schedule:
        lessons += (f'<b>Название</b>: {lesson.title}\n'
                    f'<b>Аудитория</b>: {lesson.auditorium}\n'
                    f'<b>Преподаватель</b>: {lesson.teacher}\n'
                    f'<b>Начало пары</b>: {lesson.time_start.strftime("%H:%M")}\n'
                    f'<b>Конец пары</b>: {lesson.time_end.strftime("%H:%M")}\n')

    return lessons


def format_day_name(day_name):
    if day_name == 'MONDAY':
        return 'Понедельник'
    elif day_name == 'TUESDAY':
        return 'Вторник'
    elif day_name == 'WEDNESDAY':
        return 'Среда'
    elif day_name == 'THURSDAY':
        return 'Четверг'
    elif day_name == 'FRIDAY':
        return 'Пятница'
    elif day_name == 'SATURDAY':
        return 'Суббота'
    elif day_name == 'SUNDAY':
        return 'Воскресенье'
    else:
        return day_name
