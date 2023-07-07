from aiogram.types import Message, InlineKeyboardButton, InlineKeyboardMarkup, CallbackQuery
<<<<<<< Updated upstream
from .Icon import *
from .statement import *
from .States import *
from aiogram.dispatcher import FSMContext
import logging
from logging import INFO

logger = logging.getLogger()
=======
from aiogram.dispatcher import FSMContext

from rabbit.rabbitmq import send_user_service_message
from interface.Icon import *
from interface.States import *
from interface.statement import *
>>>>>>> Stashed changes


def start_inline_keyboard() -> InlineKeyboardMarkup:
    create_statement_btn = InlineKeyboardButton('Подать заявку на добавление', callback_data='create_statement')
    help_btn = InlineKeyboardButton('Помощь', callback_data='help')
    return InlineKeyboardMarkup(row_width=2).add(create_statement_btn, help_btn)


def create_statement_inline_keyboard() -> InlineKeyboardMarkup:
    back_btn = InlineKeyboardButton('Назад', callback_data='cancel_statement')
    create_statement_btn = InlineKeyboardButton('Отправить заявку', callback_data='send_statement')
    return InlineKeyboardMarkup(row_width=2).add(create_statement_btn, back_btn)


def help_inline_keyboard() -> InlineKeyboardMarkup:
    create_statement_btn = InlineKeyboardButton('Подать заявку на добавление', callback_data='create_statement')
    iit_contacts = InlineKeyboardButton('Контакты ИИТ', url='https://iit.csu.ru/')
    return InlineKeyboardMarkup(row_width=2).add(create_statement_btn, iit_contacts)


<<<<<<< Updated upstream
def confirmation_inline_keyboard() -> InlineKeyboardMarkup:
    confirmation_btn = InlineKeyboardButton('Уведомление о событии получено ' + Icon.CHECK.value, callback_data='confirmation')
=======
def confirmation_inline_keyboard(event_id) -> InlineKeyboardMarkup:
    confirmation_btn = InlineKeyboardButton('Уведомление о событии получено ' + Icon.CHECK.value,
                                            callback_data=f'confirmation:{event_id}')
>>>>>>> Stashed changes
    return InlineKeyboardMarkup(row_width=2).add(confirmation_btn)


def mark_inline_keyboard() -> InlineKeyboardMarkup:
<<<<<<< Updated upstream
    one_star_btn = InlineKeyboardButton(Icon.STAR.value, callback_data='send_mark_one')
    two_star_btn = InlineKeyboardButton(Icon.TWO_STAR.value, callback_data='send_mark_two')
    three_star_btn = InlineKeyboardButton(Icon.THREE_STAR.value, callback_data='send_mark_three', )
    four_star_btn = InlineKeyboardButton(Icon.FOUR_STAR.value, callback_data='send_mark_four')
    five_star_btn = InlineKeyboardButton(Icon.FIVE_STAR.value, callback_data='send_mark_five')
    return InlineKeyboardMarkup(row_width=2).add(one_star_btn, two_star_btn, three_star_btn, four_star_btn, five_star_btn)
=======
    one_star_btn = InlineKeyboardButton(Icon.STAR.value, callback_data="mark:1")
    two_star_btn = InlineKeyboardButton(Icon.TWO_STAR.value, callback_data='mark:2')
    three_star_btn = InlineKeyboardButton(Icon.THREE_STAR.value, callback_data='mark:3')
    four_star_btn = InlineKeyboardButton(Icon.FOUR_STAR.value, callback_data='mark:4')
    five_star_btn = InlineKeyboardButton(Icon.FIVE_STAR.value, callback_data='mark:5')
    return InlineKeyboardMarkup(row_width=2).add(one_star_btn, two_star_btn, three_star_btn, four_star_btn,
                                                 five_star_btn)
>>>>>>> Stashed changes


def comment_inline_keyboard() -> InlineKeyboardMarkup:
    write_comment_btn = InlineKeyboardButton('Отправить комментарий', callback_data='send_comment')
    cancel_btn = InlineKeyboardButton('Нет', callback_data='cancel_comment')
    return InlineKeyboardMarkup(row_width=2).add(write_comment_btn, cancel_btn)


def student_main_inline_keyboard() -> InlineKeyboardMarkup:
    get_schedule = InlineKeyboardButton('Получить расписание', callback_data='get_schedule')
    notification_settings = InlineKeyboardButton('Настройка уведомлений', callback_data='notification_settings')
    return InlineKeyboardMarkup(row_width=2).add(get_schedule, notification_settings)


async def start_message(message: Message):
    await message.answer(f'Привет, я бот института информационных технологий. Я помогу тебе узнать свое расписание и '
                         f'буду сообщать тебе о главных мероприятиях института. Нажми "Подать заявку на добавление" и'
                         f' заполни форму.', reply_markup=start_inline_keyboard())
    logger.info(f'user id: {message.from_user.id} /start')


async def application_is_approved(message: Message):
    await message.answer(f'Заявка одобрена!', reply_markup=student_main_inline_keyboard())
    logger.info(f'user id: {message.from_user.id} Заявка одобрена')


async def help_message(message: Message):
    await message.answer(f'Чем могу помочь?', reply_markup=help_inline_keyboard())
    logger.info(f'user id: {message.from_user.id} /help')


async def feedback_message(message: Message):
    await message.answer(f'Обратная связь по прошедшему мероприятию, как все прошло?', reply_markup=mark_inline_keyboard())
    logger.info(f'user id: {message.from_user.id} обратная связь')


async def pars_statement(message: Message, state: FSMContext):
    statement = message.text
    index = statement.find('_')
    statement_to_send = Statement(message.from_user.id, statement[:index], statement[index+1:])
    await state.finish()
<<<<<<< Updated upstream
    logger.info(f'user id: {message.from_user.id} спрарсили заявку')
=======


async def pars_comment(message:Message, state: FSMContext):
    comment = message.text
    await state.finish()
>>>>>>> Stashed changes


async def pars_comment(message:Message, state: FSMContext):
    comment = message.text
    await state.finish()
    logger.info(f'user id: {message.from_user.id} спарсили комментарий')


async def callback_query_statement(call: CallbackQuery, state):
    await call.message.edit_text(text='Отправь мне свое ФИО и группу в формате: "ФИО_группа".\nНапример: "Иванов Иван '
                                      'Иванович_ПрИ-200"\nПосле того как отправишь анкету нажми "Отправить заявку"',
                                 reply_markup=create_statement_inline_keyboard())
    await States.statement.set()
<<<<<<< Updated upstream
    logger.info(f'user id: {call.from_user.id} /подать заявку')
=======
>>>>>>> Stashed changes


async def callback_query_help(call: CallbackQuery):
    await help_message(call.message)
    logger.info(f'user id: {call.from_user.id} /help')


async def callback_query_send_statement(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text(text='Заявка успешно отправлена! ' + Icon.CHECK.value)
<<<<<<< Updated upstream
    logger.info(f'user id: {call.from_user.id} отправили заявку')


async def callback_query_mark_one(call: CallbackQuery):
    await call.message.edit_text(text='Спасибо за оценку! Хочешь оставить комментарий? Напиши свой отзыв и нажми "Отправить комментарий"',
                                 reply_markup=comment_inline_keyboard())
    await States.comment.set()
    logger.info(f'user id: {call.from_user.id} дал оценку 1')


async def callback_query_mark_two(call: CallbackQuery):
    await call.message.edit_text(text='Спасибо за оценку! Хочешь оставить комментарий? Напиши свой отзыв и нажми "Отправить комментарий"',
                                 reply_markup=comment_inline_keyboard())
    await States.comment.set()
    logger.info(f'user id: {call.from_user.id} дал оценку 2')


async def callback_query_mark_three(call: CallbackQuery):
    await call.message.edit_text(text='Спасибо за оценку! Хочешь оставить комментарий? Напиши свой отзыв и нажми "Отправить комментарий"',
                                 reply_markup=comment_inline_keyboard())
    await States.comment.set()
    logger.info(f'user id: {call.from_user.id} дал оценку 3')


async def callback_query_mark_four(call: CallbackQuery):
    await call.message.edit_text(text='Спасибо за оценку! Хочешь оставить комментарий? Напиши свой отзыв и нажми "Отправить комментарий"',
                                 reply_markup=comment_inline_keyboard())
    await States.comment.set()
    logger.info(f'user id: {call.from_user.id} дал оценку 4')


async def callback_query_mark_five(call: CallbackQuery):
    await call.message.edit_text(text='Спасибо за оценку! Хочешь оставить комментарий? Напиши свой отзыв и нажми "Отправить комментарий"',
                                 reply_markup=comment_inline_keyboard())
    await States.comment.set()
    logger.info(f'user id: {call.from_user.id} дал оценку 5')

    
=======
    state.finish()


async def callback_query_mark(call: CallbackQuery):
    await call.message.edit_text(text='Спасибо за оценку! Хочешь оставить комментарий? Напиши свой отзыв и нажми "Отправить комментарий"',
                                 reply_markup=comment_inline_keyboard())
    mark = int(call.data[5])
    await States.comment.set()


>>>>>>> Stashed changes
async def callback_query_send_comment(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text(text='Комментарий успешно отправлен! ' + Icon.CHECK.value,
                                 reply_markup=student_main_inline_keyboard())
    await state.finish()
<<<<<<< Updated upstream
    logger.info(f'user id: {call.from_user.id} отправили комментарий')
=======
>>>>>>> Stashed changes


async def callback_query_cancel_comment(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text(text='Отказ от комментария',
                                 reply_markup=student_main_inline_keyboard())
    await state.finish()
<<<<<<< Updated upstream
    logger.info(f'user id: {call.from_user.id} отказался от комментария')


async def callback_query_confirmation_of_notification(call: CallbackQuery):
    logger.info(f'user id: {call.from_user.id} подтвердил получение уведомления')
=======
>>>>>>> Stashed changes


async def callback_query_cancel_statement(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text(text=f'Привет, я бот института информационных технологий. Я помогу тебе узнать свое расписание и '
                         f'буду сообщать тебе о главных мероприятиях института. Нажми "Подать заявку на добавление" и'
                         f' заполни форму.', reply_markup=start_inline_keyboard())
    await state.finish()
<<<<<<< Updated upstream
    logger.info(f'user id: {call.from_user.id} /назад')
=======


async def callback_query_confirmation_of_notification(call: CallbackQuery):
    event_id = call.data[13:]
>>>>>>> Stashed changes
