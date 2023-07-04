import logging

import aioamqp
from aioamqp.channel import Channel
from pika.exchange_type import ExchangeType
# from service import notification_service as service

logger = logging.getLogger()

QUEUE = 'user-service'
EXCHANGE = 'service.user'
ROUTING_KEY = 'user-routing-key'

channel: Channel


async def send_message(message):
    await channel.publish(message,
                          exchange_name='service.notification',
                          routing_key='notification-routing-key')


async def callback(channel: Channel, body: bytes, envelope, properties):
    try:
        print(body)
    except Exception as e:
        logger.error(e)
    await channel.basic_client_ack(delivery_tag=envelope.delivery_tag)


async def connect_to_broker():
    try:
        transport, protocol = await aioamqp.connect(host='localhost',
                                                    port=5672,
                                                    login='guest',
                                                    password='guest')
    except aioamqp.AmqpClosedConnection:
        logger.info('Connection closed')
        return

    global channel
    channel = await protocol.channel()
    logger.info('Rabbit starts')
    await channel.exchange_declare(exchange_name=EXCHANGE, type_name=ExchangeType.topic.name)
    await channel.queue(queue_name=QUEUE, durable=True)
    await channel.queue_bind(queue_name=QUEUE, exchange_name=EXCHANGE, routing_key=ROUTING_KEY)
    await channel.basic_consume(callback=callback, queue_name=QUEUE)
