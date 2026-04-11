import json
import logging
from typing import Any

import aio_pika
from aio_pika import ExchangeType

from app.config import settings

logger = logging.getLogger(__name__)

_connection: aio_pika.abc.AbstractRobustConnection | None = None
_channel: aio_pika.abc.AbstractChannel | None = None
_exchange: aio_pika.abc.AbstractExchange | None = None


async def connect() -> None:
    global _connection, _channel, _exchange
    try:
        _connection = await aio_pika.connect_robust(settings.rabbitmq_url)
        _channel = await _connection.channel()
        _exchange = await _channel.declare_exchange(
            settings.rabbitmq_exchange,
            ExchangeType.TOPIC,
            durable=True,
        )
        logger.info("RabbitMQ connected — exchange: %s", settings.rabbitmq_exchange)
    except Exception as exc:
        logger.warning("RabbitMQ unavailable at startup: %s — events will be skipped", exc)


async def disconnect() -> None:
    global _connection
    if _connection and not _connection.is_closed:
        await _connection.close()


async def publish_event(routing_key: str, payload: dict[str, Any]) -> None:
    if _exchange is None:
        logger.warning("RabbitMQ exchange not ready — skipping event: %s", routing_key)
        return
    try:
        message = aio_pika.Message(
            body=json.dumps(payload, default=str).encode(),
            content_type="application/json",
            delivery_mode=aio_pika.DeliveryMode.PERSISTENT,
        )
        await _exchange.publish(message, routing_key=routing_key)
        logger.debug("Published event [%s]: %s", routing_key, payload)
    except Exception as exc:
        logger.error("Failed to publish event [%s]: %s", routing_key, exc)
