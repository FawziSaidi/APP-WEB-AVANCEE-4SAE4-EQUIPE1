# Ads-Service Integration

Drop the contents of `src/main/java/com/esprit/ads/integration/` into the matching package in your ads-service source tree.

## What's included

| File | Purpose |
|---|---|
| `config/RabbitMQIntegrationConfig.java` | Declares `app.events` exchange, `transaction.completed.ads` queue, JSON converter |
| `config/FeignClientConfig.java` | Propagates the incoming JWT Bearer token to all outbound Feign calls |
| `client/UserServiceClient.java` | Feign client → User service |
| `client/ForumServiceClient.java` | Feign client → Forum service |
| `client/TransactionServiceClient.java` | Feign client → Transaction service |
| `dto/UserDto.java` | Response DTO for User service |
| `dto/ForumPostDto.java` | Response DTO for Forum service |
| `dto/TransactionDto.java` | Response DTO for Transaction service |
| `dto/TransactionListDto.java` | Paginated list response DTO |
| `dto/TransactionEventDto.java` | RabbitMQ event payload from transaction-service |
| `listener/TransactionEventListener.java` | Consumes `transaction.completed` events |

## pom.xml dependencies to add

```xml
<!-- Spring Cloud OpenFeign -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<!-- Spring AMQP / RabbitMQ -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

And in `<dependencyManagement>` ensure you have a Spring Cloud BOM, e.g.:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dependencies</artifactId>
    <version>2023.0.1</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

## Main class annotation

```java
@SpringBootApplication
@EnableFeignClients(basePackages = "com.esprit.ads.integration.client")
public class AdsServiceApplication { ... }
```

## application.yml additions

Merge `src/main/resources/application-integration.yml` into your existing `application.yml`.

## URL defaults (override in application.yml)

| Property | Default |
|---|---|
| `services.user-service.url` | `http://user-service:8082` |
| `services.forum-service.url` | `http://forum-service:8083` |
| `services.transaction-service.url` | `http://transaction-service:8084` |

Adjust these to match your docker-compose service names or gateway routes.
