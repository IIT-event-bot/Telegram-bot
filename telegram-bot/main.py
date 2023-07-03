import asyncio
from logging import INFO, WARNING

from aiogram import Bot, Dispatcher
from dotenv import load_dotenv, dotenv_values

from interface.user_interface import *
from rabbit.rabbitmq import *

logger = logging.getLogger()
config = dotenv_values()
bot = Bot(config['BOT_TOKEN'], validate_token=True, parse_mode="HTML")


def __config_logger():
    file_log = logging.FileHandler('notification-service.log')
    console_log = logging.StreamHandler()
    FORMAT = '[%(levelname)s] %(asctime)s : %(message)s | %(filename)s'
    logging.getLogger('apscheduler.scheduler').setLevel(WARNING)
    logging.basicConfig(level=INFO,
                        format=FORMAT,
                        handlers=(file_log, console_log),
                        datefmt='%d-%m-%y - %H:%M:%S')


async def main():
    dp = Dispatcher(bot=bot)
    dp.register_message_handler(start_message, commands=['start'])
    dp.register_message_handler(help_message, commands=['help'])
    dp.register_callback_query_handler(callback_query_statement, lambda call: call.data == 'create_statement')
    dp.register_callback_query_handler(callback_query_help, lambda call: call.data == 'help')
    dp.register_callback_query_handler(callback_query_send_statement, lambda call: call.data == 'send_statement')

    logger.info('Bot starts')
    await dp.start_polling()


if __name__ == '__main__':
    __config_logger()
    load_dotenv('.env')
    loop = asyncio.get_event_loop()
    loop.run_until_complete(connect_to_broker())
    loop.run_until_complete(main())
    loop.run_forever()
