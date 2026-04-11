from datetime import datetime
from decimal import Decimal

from pydantic import BaseModel, Field, field_validator


class TransactionCreate(BaseModel):
    receiver_id: str = Field(..., description="Keycloak user ID of the recipient")
    amount: Decimal = Field(..., gt=0, decimal_places=2, description="Amount to transfer (must be > 0)")
    currency: str = Field(default="TND", max_length=3)
    reference: str | None = Field(default=None, max_length=100)

    @field_validator("currency")
    @classmethod
    def currency_upper(cls, v: str) -> str:
        return v.upper()


class TransactionResponse(BaseModel):
    id: str
    sender_id: str
    receiver_id: str
    amount: Decimal
    currency: str
    status: str
    reference: str | None
    created_at: datetime

    model_config = {"from_attributes": True}


class TransactionListResponse(BaseModel):
    transactions: list[TransactionResponse]
    total: int
