import json
import logging
import os
from typing import Any

import aioamqp
from aioamqp.channel import Channel

from interface import user_interface as ui
from main import bot

logger = logging.getLogger()


class RabbitMQClient:
    QUEUE = 'telegram-notification'
    EXCHANGE = 'service.telegram'
    ROUTING_KEY = 'telegram-routing-key'

    channel: Channel

    async def send_message_to_user_service(self, message):
        await self.channel.publish(message, exchange_name='service.user', routing_key='user-routing-key')

    async def send_message_to_event_service(self, message):
        await self.channel.publish(message, exchange_name='service.event', routing_key='event-routing-key')

    async def __callback(self, channel: Channel, body: bytes, envelope, properties):
        json_body = str(body.decode('utf-8'))
        json_message: dict[str, Any] = json.loads(json.loads(json_body, strict=False), strict=False)
        try:
            await self.handle_message(json_message)
        except Exception as e:
            logger.error(e)
        await self.channel.basic_client_ack(delivery_tag=envelope.delivery_tag)

    @classmethod
    async def handle_message(cls, message):
        if message['type'] == 'INFO':
            await bot.send_message(chat_id=message['chatId'],
                                   text=f'<b>{message["title"]}</b>\n{message["text"]}',
                                   reply_markup=ui.confirmation_inline_keyboard(message['eventId']))
        elif message['type'] == 'SYS_INFO':
            if ui.user_service.is_student(message['chatId']):
                await bot.send_message(chat_id=message['chatId'],
                                       text=f'<b>{message["title"]}</b>\n{message["text"]}')
            else:
                await bot.send_message(chat_id=message['chatId'],
                                       text=f'<b>{message["title"]}</b>\n{message["text"]}',
                                       reply_markup=ui.student_button())
        elif message['type'] == 'EVENT':
            await bot.send_message(chat_id=message['chatId'],
                                   text=f'<b>{message["title"]}</b>\n{message["text"]}',
                                   reply_markup=ui.confirmation_inline_keyboard(message['eventId']))
        elif message['type'] == 'FEEDBACK':
            await bot.send_message(chat_id=message['chatId'],
                                   text=f'<b>{message["title"]}</b>\n{message["text"]}',
                                   reply_markup=ui.mark_inline_keyboard(message['event_id']))
        elif message['type'] == 'SCHEDULE':
            await bot.send_message(chat_id=message['chatId'],
                                   text=f'<b>{message["title"]}</b>\n{message["text"]}')

    async def start_consuming(self):
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
