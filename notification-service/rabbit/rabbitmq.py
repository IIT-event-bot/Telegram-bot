import json
import logging
import os
from typing import Any

import aioamqp
from aioamqp.channel import Channel

logger = logging.getLogger()


class RabbitMQClient:
    QUEUE = 'send-notification'
    EXCHANGE = 'service.notification'
    ROUTING_KEY = 'notification-routing-key'

    channel: Channel
    service: Any

    async def send_message(self, message):
        await self.channel.publish(message,
                                   exchange_name='service.telegram',
                                   routing_key='telegram-routing-key')
        message_json = json.loads(message)
        logger.info(f"Send notification [ type: {message_json['type']}, user: {message_json['chat_id']} ]")

    async def __callback(self, channel: Channel, body: bytes, envelope, properties):
        try:
            json_body = str(body.decode('utf-8'))
            notification = self.service.convert_json_to_notification(json_body)
            await self.service.save_notification(notification=notification)
        except Exception as e:
            logger.error(e)
        await self.channel.basic_client_ack(delivery_tag=envelope.delivery_tag)

    async def connect_to_broker(self):
        try:
            transport, protocol = await aioamqp.connect(host=os.environ.get('RABBITMQ_HOST'),
                                                        port=5672,
                                                        login='guest',
                                                        password='guest')
        except aioamqp.AmqpClosedConnection:
            logger.info('Connection closed')
            return

        self.channel = await protocol.channel()

        logger.info('Rabbit starts')
        await self.channel.exchange_declare(exchange_name=self.EXCHANGE, type_name="topic")
        await self.channel.queue(queue_name=self.QUEUE, durable=True)
        await self.channel.queue_bind(queue_name=self.QUEUE, exchange_name=self.EXCHANGE, routing_key=self.ROUTING_KEY)
        await self.channel.basic_consume(callback=self.__callback, queue_name=self.QUEUE)


instance_rabbit_client = RabbitMQClient()


def get_instance() -> RabbitMQClient:
    return instance_rabbit_client
