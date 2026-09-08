import uuid
from datetime import datetime

from sqlalchemy import DateTime, Numeric, String, func
from sqlalchemy.orm import Mapped, mapped_column

from app.database import Base


class Transaction(Base):
    __tablename__ = "transactions"

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    sender_id: Mapped[str] = mapped_column(String(255), nullable=False, index=True)
    receiver_id: Mapped[str] = mapped_column(String(255), nullable=False, index=True)
    amount: Mapped[float] = mapped_column(Numeric(15, 2), nullable=False)
    currency: Mapped[str] = mapped_column(String(3), default="TND", nullable=False)
    status: Mapped[str] = mapped_column(String(20), default="COMPLETED", nullable=False)
    reference: Mapped[str | None] = mapped_column(String(100), nullable=True)
    created_at: Mapped[datetime] = mapped_column(DateTime, server_default=func.now(), nullable=False)
