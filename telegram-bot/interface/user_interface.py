from aiogram.types import Message, InlineKeyboardButton, InlineKeyboardMarkup, CallbackQuery
from interface.Icon import *


def start_inline_keyboard() -> InlineKeyboardMarkup:
    create_statement_btn = InlineKeyboardButton('Подать заявку на добавление', callback_data='create_statement')
    help_btn = InlineKeyboardButton('Помощь', callback_data='help')
    return InlineKeyboardMarkup(row_width=2).add(create_statement_btn, help_btn)


def create_statement_inline_keyboard() -> InlineKeyboardMarkup:
    back_btn = InlineKeyboardButton('Назад', callback_data='cancel')
    create_statement_btn = InlineKeyboardButton('Отправить заявку', callback_data='send_statement')
    return InlineKeyboardMarkup(row_width=2).add(create_statement_btn, back_btn)


def help_inline_keyboard() -> InlineKeyboardMarkup:
    create_statement_btn = InlineKeyboardButton('Подать заявку на добавление', callback_data='create_statement')
    iit_contacts = InlineKeyboardButton('Контакты ИИТ', url='https://iit.csu.ru/')
    return InlineKeyboardMarkup(row_width=2).add(create_statement_btn, iit_contacts)


def confirmation_inline_keyboard() -> InlineKeyboardMarkup:
    confirmation_btn = InlineKeyboardButton('Уведомление о событии получено ' + Icon.CHECK.value, callback_data='confirmation')
    return InlineKeyboardMarkup(row_width=2).add(confirmation_btn)


def mark_inline_keyboard() -> InlineKeyboardMarkup:
    one_star_btn = InlineKeyboardButton(Icon.STAR.value, callback_data='send_mark')
    two_star_btn = InlineKeyboardButton(Icon.TWO_STAR.value, callback_data='send_mark')
    three_star_btn = InlineKeyboardButton(Icon.THREE_STAR.value, callback_data='send_mark')
    four_star_btn = InlineKeyboardButton(Icon.FOUR_STAR.value, callback_data='send_mark')
    five_star_btn = InlineKeyboardButton(Icon.FIVE_STAR.value, callback_data='send_mark')
    return InlineKeyboardMarkup(row_width=2).add(one_star_btn, two_star_btn, three_star_btn, four_star_btn, five_star_btn)


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


async def application_is_approved(message: Message):
    await message.answer(f'Заявка одобрена!', reply_markup=student_main_inline_keyboard())


async def help_message(message: Message):
    await message.answer(f'Чем могу помочь?', reply_markup=help_inline_keyboard())


async def feedback_message(message: Message):
    await message.answer(f'Обратная связь по прошедшему мероприятию, как все прошло?', reply_markup=mark_inline_keyboard())


async def create_statement(message: Message):
    await message.answer('Пример заявки...', reply=True)


async def callback_query_statement(call: CallbackQuery):
    await call.message.edit_text(text='Отправь мне свое ФИО и группу в формате: "ФИО_группа".\nНапример: "Иванов Иван '
                                      'Иванович_ПрИ-200"\nПосле того как отправишь анкету нажми "Отправить заявку"',
                                 reply_markup=create_statement_inline_keyboard())


async def callback_query_help(call: CallbackQuery):
    await help_message(call.message)


async def callback_query_send_statement(call: CallbackQuery):
    await call.message.edit_text(text='Заявка успешно отправлена! ' + Icon.CHECK.value)


async def callback_query_mark(call: CallbackQuery):
    await call.message.edit_text(text='Спасибо за оценку! Хочешь оставить комментарий? Напиши свой отзыв и нажми "Отправить комментарий"',
                                 reply_markup=comment_inline_keyboard())

    
async def callback_query_send_comment(call: CallbackQuery):
    await call.message.edit_text(text='Комментарий успешно отправлен! ' + Icon.CHECK.value,
                                 reply_markup=student_main_inline_keyboard())


async def callback_query_cancel_comment(call: CallbackQuery):
    await call.message.edit_text(text='Отказ от комментария',
                                 reply_markup=student_main_inline_keyboard())