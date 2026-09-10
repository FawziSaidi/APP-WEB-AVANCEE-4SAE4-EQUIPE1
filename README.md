# Architecture microservices distribuée avec Spring Cloud, Keycloak et RabbitMQ — Plateforme forum & transactions P2P

All backend microservices unified under one branch, fully Dockerized.

## Services

| Service | Port | Stack | Description |
|---|---|---|---|
| eureka-server | 8761 | Spring Cloud Eureka | Service discovery |
| api-gateway | 8222 | Spring Cloud Gateway | Routing + Keycloak JWT |
| user-service | 8081 | Spring Boot + MySQL | User management + Keycloak admin |
| publication-service | 8082 | Spring Boot + MySQL | Forum publications |
| commentaire-service | 8083 | Spring Boot + MySQL | Forum comments |
| reaction-service | 8084 | Spring Boot + MySQL | Forum reactions |
| transaction-service | 8086 | FastAPI + SQLite | Peer-to-peer money transfers |

## Infrastructure

| Component | Port | Notes |
|---|---|---|
| MySQL | 3306 | Databases auto-created |
| RabbitMQ | 5672 / 15672 (mgmt) | Exchange: `app.events` |
| Keycloak | 8180 | Realm: `ProlanceRealm`, admin/admin |

## Quick Start

```bash
docker-compose up --build
```

## Verify

- Eureka dashboard: http://localhost:8761
- Keycloak admin: http://localhost:8180
- RabbitMQ mgmt: http://localhost:15672 (guest/guest)
- Gateway health: http://localhost:8222/actuator/health
- Transaction Swagger: http://localhost:8086/docs
- Transaction health: http://localhost:8086/health

## Transaction Service API (via Gateway)

```
POST  http://localhost:8222/api/transactions/send     (Bearer JWT)
GET   http://localhost:8222/api/transactions/history   (Bearer JWT)
GET   http://localhost:8222/api/transactions/{id}      (Bearer JWT)
```
