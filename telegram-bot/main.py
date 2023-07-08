import asyncio
from logging import WARNING, INFO

from aiogram import Bot, Dispatcher
from aiogram.contrib.fsm_storage.memory import MemoryStorage
from dotenv import load_dotenv, dotenv_values

from interface.user_interface import *
from rabbit.rabbitmq import *

logger = logging.getLogger()
config = dotenv_values()
bot = Bot(os.environ.get('TELEGRAM_BOT_TOKEN'), validate_token=True, parse_mode="HTML")


def __config_logger():
    file_log = logging.FileHandler('telegram-bot.log')
    console_log = logging.StreamHandler()
    FORMAT = '[%(levelname)s] %(asctime)s : %(message)s | %(filename)s'
    logging.getLogger('apscheduler.scheduler').setLevel(WARNING)
    logging.basicConfig(level=INFO,
                        format=FORMAT,
                        handlers=(file_log, console_log),
                        datefmt='%d-%m-%y - %H:%M:%S')


async def main():
    dp = Dispatcher(bot=bot, storage=MemoryStorage())
    dp.register_message_handler(start_message, commands=['start'])
    dp.register_message_handler(help_message, commands=['help'])

    dp.register_message_handler(add_statement, state=States.add_statement)
    dp.register_message_handler(parse_comment, state=States.comment)

    dp.register_callback_query_handler(callback_query_statement, lambda call: call.data == 'create_statement')
    dp.register_callback_query_handler(callback_query_help, lambda call: call.data == 'help')
    dp.register_callback_query_handler(callback_query_send_statement, lambda call: call.data == 'send_statement')
    dp.register_callback_query_handler(callback_query_cancel_comment, lambda call: call.data == 'cancel_comment')
    dp.register_callback_query_handler(callback_query_send_comment, lambda call: call.data == 'send_comment')
    dp.register_callback_query_handler(add_comment, lambda call: 'add_comment' in call.data)
    dp.register_callback_query_handler(callback_query_confirmation_of_notification,
                                       lambda call: call.data == 'confirmation')
    dp.register_callback_query_handler(callback_query_mark, lambda call: 'mark' in call.data)
    dp.register_callback_query_handler(callback_query_cancel_statement, lambda call: call.data == 'cancel_statement')
    logger.info('Bot starts')
    await dp.start_polling()


if __name__ == '__main__':
    __config_logger()
    load_dotenv('../.env')
    loop = asyncio.get_event_loop()
    rabbit = instance_rabbit_client
    loop.run_until_complete(rabbit.start_consuming())
    loop.run_until_complete(main())
    loop.run_forever()
