import json
import logging

import aioamqp
from aioamqp.channel import Channel
from main import bot
from pika.exchange_type import ExchangeType

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
    message_text = f'<b>{json_message["title"]}</b>\n{json_message["text"]}'
    logger.info(f'Message type {json_message["type"]}')
    await bot.send_message(chat_id=json_message['chat_id'],
                           text=message_text)
    await channel.basic_client_ack(delivery_tag=envelope.delivery_tag)


async def connect_to_broker():
    try:
        transport, protocol = await aioamqp.connect(host='localhost', port=5672, login='guest', password='guest')
    except aioamqp.AmqpClosedConnection:
        logger.info('Connection closed')
        return

    global channel
    channel = await protocol.channel()
    logger.info('Rabbit starts')
    await channel.exchange_declare(exchange_name=EXCHANGE, type_name=ExchangeType.topic.name)
    await channel.queue(queue_name=QUEUE, durable=True)
    await channel.basic_consume(callback=callback, queue_name=QUEUE)
