from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8", extra="ignore")

    database_url: str = "sqlite+aiosqlite:///./data/transactions.db"

    keycloak_url: str = "http://keycloak:8080"
    keycloak_issuer_url: str = ""  # if empty, falls back to keycloak_url
    keycloak_realm: str = "ProlanceRealm"
    keycloak_client_id: str = "api-gateway"

    user_service_url: str = "http://user-service:8081"

    rabbitmq_url: str = "amqp://guest:guest@rabbitmq:5672/"
    rabbitmq_exchange: str = "app.events"
    rabbitmq_transaction_queue: str = "transaction.completed"

    app_host: str = "0.0.0.0"
    app_port: int = 8086
    cors_origins: str = "http://localhost:4200"

    @property
    def cors_origins_list(self) -> list[str]:
        return [o.strip() for o in self.cors_origins.split(",")]

    @property
    def keycloak_jwks_uri(self) -> str:
        return f"{self.keycloak_url}/realms/{self.keycloak_realm}/protocol/openid-connect/certs"

    @property
    def keycloak_issuer(self) -> str:
        base = self.keycloak_issuer_url or self.keycloak_url
        return f"{base}/realms/{self.keycloak_realm}"


settings = Settings()
