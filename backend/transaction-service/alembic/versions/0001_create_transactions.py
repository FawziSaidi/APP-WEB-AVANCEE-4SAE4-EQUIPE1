"""create transactions table

Revision ID: 0001
Revises:
Create Date: 2025-01-01 00:00:00.000000
"""
from typing import Sequence, Union

import sqlalchemy as sa
from alembic import op

revision: str = "0001"
down_revision: Union[str, None] = None
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    op.create_table(
        "transactions",
        sa.Column("id", sa.String(36), nullable=False),
        sa.Column("sender_id", sa.String(255), nullable=False),
        sa.Column("receiver_id", sa.String(255), nullable=False),
        sa.Column("amount", sa.Numeric(15, 2), nullable=False),
        sa.Column("currency", sa.String(3), nullable=False, server_default="TND"),
        sa.Column("status", sa.String(20), nullable=False, server_default="COMPLETED"),
        sa.Column("reference", sa.String(100), nullable=True),
        sa.Column("created_at", sa.DateTime(), nullable=False, server_default=sa.text("CURRENT_TIMESTAMP")),
        sa.PrimaryKeyConstraint("id"),
        sa.CheckConstraint("amount > 0", name="ck_transactions_amount_positive"),
    )
    op.create_index("ix_transactions_sender_id", "transactions", ["sender_id"])
    op.create_index("ix_transactions_receiver_id", "transactions", ["receiver_id"])


def downgrade() -> None:
    op.drop_index("ix_transactions_receiver_id", table_name="transactions")
    op.drop_index("ix_transactions_sender_id", table_name="transactions")
    op.drop_table("transactions")
