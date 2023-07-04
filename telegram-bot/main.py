import asyncio
import logging
from logging import INFO

from aiogram import Bot, Dispatcher
from dotenv import load_dotenv, dotenv_values

from interface.user_interface import *

logger = logging.getLogger()


def __config_logger():
    file_log = logging.FileHandler('telegram-bot.log')
    console_log = logging.StreamHandler()
    FORMAT = '[%(levelname)s] %(asctime)s : %(message)s | %(filename)s'
    logging.basicConfig(level=INFO,
                        format=FORMAT,
                        handlers=(file_log, console_log),
                        datefmt='%d-%m-%y - %H:%M:%S')


async def main():
    config = dotenv_values()
    bot = Bot(config['BOT_TOKEN'], validate_token=True, parse_mode="HTML")
    dp = Dispatcher(bot=bot)
    dp.register_message_handler(start_message, commands=['start'])
    dp.register_message_handler(help_message, commands=['help'])
    dp.register_callback_query_handler(callback_query_statement, lambda call: call.data == 'create_statement')
    dp.register_callback_query_handler(callback_query_help, lambda call: call.data == 'help')
    dp.register_callback_query_handler(callback_query_send_statement, lambda call: call.data == 'send_statement')
    dp.register_callback_query_handler(callback_query_mark, lambda call: call.data == 'send_mark')
    dp.register_callback_query_handler(callback_query_cancel_comment, lambda call: call.data == 'cancel_comment')
    dp.register_callback_query_handler(callback_query_send_comment, lambda call: call.data == 'send_comment')
    logger.info('Bot starts')
    await dp.start_polling()


if __name__ == '__main__':
    __config_logger()
    load_dotenv('.env')
    asyncio.run(main())
