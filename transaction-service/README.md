# Transaction Service

Peer-to-peer money transfer microservice built with **FastAPI + SQLite + Poetry**.

## Stack
- Python 3.11 / FastAPI / SQLAlchemy 2 async
- SQLite (WAL mode, volume-mounted) — via `aiosqlite`
- Alembic migrations
- Keycloak JWT validation (JWKS)
- RabbitMQ event publishing via `aio-pika`
- Docker / docker-compose

## Quick Start (local)

```bash
# 1. Install dependencies
poetry install

# 2. Copy and configure env
cp .env.example .env
# Edit KEYCLOAK_URL, KEYCLOAK_REALM, USER_SERVICE_URL, RABBITMQ_URL as needed

# 3. Create data dir
mkdir -p data

# 4. Run migrations
poetry run alembic upgrade head

# 5. Start dev server
poetry run uvicorn app.main:app --reload --port 8084
```

## Docker

```bash
# Standalone (includes Keycloak + RabbitMQ)
docker-compose up --build

# Or if you have a shared microservices-net already running:
docker-compose up --build transaction-service
```

## API Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/health` | None | Health check |
| `POST` | `/api/transactions/send` | Bearer JWT | Send money to another user |
| `GET` | `/api/transactions/history` | Bearer JWT | Caller's transaction history |
| `GET` | `/api/transactions/{id}` | Bearer JWT | Single transaction detail |

Swagger UI: http://localhost:8084/docs

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `DATABASE_URL` | `sqlite+aiosqlite:///./data/transactions.db` | SQLite async URL |
| `KEYCLOAK_URL` | `http://keycloak:8080` | Keycloak base URL |
| `KEYCLOAK_REALM` | `esprit-realm` | Realm name |
| `KEYCLOAK_CLIENT_ID` | `transaction-service` | Client ID for audience check |
| `USER_SERVICE_URL` | `http://user-service:8082` | User service base URL |
| `RABBITMQ_URL` | `amqp://guest:guest@rabbitmq:5672/` | RabbitMQ connection |
| `RABBITMQ_EXCHANGE` | `app.events` | Topic exchange name |
| `CORS_ORIGINS` | `http://localhost:4200` | Comma-separated allowed origins |

## API Gateway Integration

Add this route to your Spring Cloud Gateway `application.yml`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: transaction-service
          uri: http://transaction-service:8084
          predicates:
            - Path=/api/transactions/**
          filters:
            - RewritePath=/api/transactions/(?<segment>.*), /api/transactions/$\{segment}
```

For Zuul, add to `application.yml`:

```yaml
zuul:
  routes:
    transaction-service:
      path: /api/transactions/**
      url: http://transaction-service:8084
      strip-prefix: false
```
