import json
import logging
import os

import aioamqp
from aioamqp.channel import Channel
from main import bot
from interface.user_interface import *

logger = logging.getLogger()

QUEUE = 'receive-notification'
EXCHANGE = 'service.telegram'
ROUTING_KEY = 'telegram-routing-key'

channel: Channel


async def send_user_service_message(message):
    await channel.publish(message, exchange_name='service.user', routing_key='user-routing-key')


async def callback(channel: Channel, body: bytes, envelope, properties):
    json_body = str(body.decode('utf-8'))
    json_message = json.loads(json_body)
    if json_message['type'] == 'INFO':
        await bot.send_message(chat_id=json_message['chat_id'],
                               text=f'<b>{json_message["title"]}</b>\n{json_message["text"]}',
                               reply_markup=student_main_inline_keyboard())
    if json_message['type'] == 'EVENT':
        await bot.send_message(chat_id=json_message['chat_id'],
                               text=f'<b>{json_message["title"]}</b>\n{json_message["text"]}',
                               reply_murkup=confirmation_inline_keyboard(json_message['event_id']))
    if json_message['type'] == 'FEEDBACK':
        await bot.send_message(chat_id=json_message['chat_id'],
                               text=f'<b>{json_message["title"]}</b>\n{json_message["text"]}',
                               reply_markup=mark_inline_keyboard())


async def connect_to_broker():
    try:
        transport, protocol = await aioamqp.connect(host=os.environ.get('RABBITMQ_HOST'), port=5672, login='guest', password='guest')
    except aioamqp.AmqpClosedConnection:
        logger.info('Connection closed')
        return

    global channel
    channel = await protocol.channel()
    logger.info('Rabbit starts')
    await channel.exchange_declare(exchange_name=EXCHANGE, type_name="topic")
    await channel.queue(queue_name=QUEUE, durable=True)
    await channel.queue_bind(queue_name=QUEUE, exchange_name=EXCHANGE, routing_key=ROUTING_KEY)
    await channel.basic_consume(callback=callback, queue_name=QUEUE)
