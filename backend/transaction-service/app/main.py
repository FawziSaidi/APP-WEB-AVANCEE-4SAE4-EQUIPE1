import logging
from contextlib import asynccontextmanager
from typing import AsyncIterator

from fastapi import FastAPI
from app.config import settings
from app.database import init_db
from app.messaging.rabbitmq import connect as rabbitmq_connect
from app.messaging.rabbitmq import disconnect as rabbitmq_disconnect
from app.routers.transactions import router as transactions_router

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI) -> AsyncIterator[None]:
    logger.info("Starting transaction-service …")
    await init_db()
    await rabbitmq_connect()
    yield
    logger.info("Shutting down transaction-service …")
    await rabbitmq_disconnect()


app = FastAPI(
    title="Transaction Service",
    description="Peer-to-peer money transfer microservice",
    version="0.1.0",
    lifespan=lifespan,
)

app.include_router(transactions_router, prefix="/api")


@app.get("/health", tags=["health"])
async def health() -> dict[str, str]:
    return {"status": "UP", "service": "transaction-service"}
