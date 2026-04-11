from typing import Any

from fastapi import APIRouter, Depends, Query
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.schemas.transaction import TransactionCreate, TransactionListResponse, TransactionResponse
from app.services.auth import get_current_user, get_raw_token
from app.services.transaction_service import create_transaction, get_transaction, list_transactions

router = APIRouter(prefix="/transactions", tags=["transactions"])


@router.post("/send", response_model=TransactionResponse, status_code=201)
async def send_money(
    payload: TransactionCreate,
    db: AsyncSession = Depends(get_db),
    current_user: dict[str, Any] = Depends(get_current_user),
    token: str = Depends(get_raw_token),
) -> TransactionResponse:
    sender_id: str = current_user["sub"]
    return await create_transaction(db, payload, sender_id, token)


@router.get("/history", response_model=TransactionListResponse)
async def transaction_history(
    skip: int = Query(default=0, ge=0),
    limit: int = Query(default=20, ge=1, le=100),
    db: AsyncSession = Depends(get_db),
    current_user: dict[str, Any] = Depends(get_current_user),
) -> TransactionListResponse:
    user_id: str = current_user["sub"]
    return await list_transactions(db, user_id, skip=skip, limit=limit)


@router.get("/{transaction_id}", response_model=TransactionResponse)
async def get_one(
    transaction_id: str,
    db: AsyncSession = Depends(get_db),
    current_user: dict[str, Any] = Depends(get_current_user),
) -> TransactionResponse:
    user_id: str = current_user["sub"]
    return await get_transaction(db, transaction_id, user_id)
