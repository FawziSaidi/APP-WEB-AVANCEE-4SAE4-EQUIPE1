import uuid
from decimal import Decimal

from fastapi import HTTPException, status
from sqlalchemy import func, or_, select
from sqlalchemy.ext.asyncio import AsyncSession

from app.messaging.rabbitmq import publish_event
from app.models.transaction import Transaction
from app.schemas.transaction import TransactionCreate, TransactionListResponse, TransactionResponse
from app.services.user_client import user_exists


async def create_transaction(
    db: AsyncSession,
    payload: TransactionCreate,
    sender_id: str,
    token: str,
) -> TransactionResponse:
    if sender_id == payload.receiver_id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Sender and receiver cannot be the same user",
        )

    receiver_ok = await user_exists(payload.receiver_id, token)
    if not receiver_ok:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Receiver user '{payload.receiver_id}' not found",
        )

    tx = Transaction(
        id=str(uuid.uuid4()),
        sender_id=sender_id,
        receiver_id=payload.receiver_id,
        amount=payload.amount,
        currency=payload.currency,
        status="COMPLETED",
        reference=payload.reference,
    )
    db.add(tx)
    await db.commit()
    await db.refresh(tx)

    await publish_event(
        "transaction.completed",
        {
            "transaction_id": tx.id,
            "sender_id": tx.sender_id,
            "receiver_id": tx.receiver_id,
            "amount": str(tx.amount),
            "currency": tx.currency,
            "created_at": str(tx.created_at),
        },
    )

    return TransactionResponse.model_validate(tx)


async def get_transaction(db: AsyncSession, transaction_id: str, user_id: str) -> TransactionResponse:
    result = await db.execute(select(Transaction).where(Transaction.id == transaction_id))
    tx = result.scalar_one_or_none()
    if tx is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Transaction not found")
    if tx.sender_id != user_id and tx.receiver_id != user_id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied")
    return TransactionResponse.model_validate(tx)


async def list_transactions(
    db: AsyncSession,
    user_id: str,
    skip: int = 0,
    limit: int = 20,
) -> TransactionListResponse:
    base_filter = or_(Transaction.sender_id == user_id, Transaction.receiver_id == user_id)

    count_result = await db.execute(select(func.count()).select_from(Transaction).where(base_filter))
    total = count_result.scalar_one()

    result = await db.execute(
        select(Transaction)
        .where(base_filter)
        .order_by(Transaction.created_at.desc())
        .offset(skip)
        .limit(limit)
    )
    txs = result.scalars().all()

    return TransactionListResponse(
        transactions=[TransactionResponse.model_validate(t) for t in txs],
        total=total,
    )
