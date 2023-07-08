import asyncio
import logging
from logging import INFO, WARNING

from apscheduler.schedulers.background import BackgroundScheduler
from dotenv import load_dotenv

from rabbit.rabbitmq import instance_rabbit_client as rabbit
from service.notification_service import check_event_time, check_event_on_next_hour


def __config_logger():
    file_log = logging.FileHandler('notification-service.log')
    console_log = logging.StreamHandler()
    FORMAT = '[%(levelname)s] %(asctime)s : %(message)s | %(filename)s - %(name)s'
    logging.getLogger('apscheduler.scheduler').setLevel(WARNING)
    logging.getLogger('apscheduler.executors').setLevel(WARNING)
    logging.getLogger('pika').setLevel(WARNING)
    logging.getLogger('aioamqp').setLevel(WARNING)
    logging.getLogger('sqlalchemy.engine').setLevel(WARNING)
    logging.basicConfig(level=INFO,
                        format=FORMAT,
                        handlers=(file_log, console_log),
                        datefmt='%d-%m-%y - %H:%M:%S')


def start_background_scheduler():
    sch = BackgroundScheduler()
    sch.add_job(check_event_time, 'interval', minutes=1)
    sch.add_job(check_event_on_next_hour, 'interval', hours=1)
    check_event_on_next_hour()
    sch.start()


async def main():
    start_background_scheduler()
    await rabbit.connect_to_broker()


if __name__ == '__main__':
    __config_logger()
    load_dotenv('.env')
    loop = asyncio.get_event_loop()
    loop.run_until_complete(main())
    loop.run_forever()
