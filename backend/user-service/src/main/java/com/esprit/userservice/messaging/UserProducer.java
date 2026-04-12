package com.esprit.userservice.messaging;

import com.esprit.userservice.config.RabbitMQConfig;
import com.esprit.userservice.dto.UserEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Producteur RabbitMQ côté user-service.
 *
 * Publie un UserEventDTO dans les CINQ queues à chaque
 * création ou modification d'un utilisateur :
 *  - user.sync.queue.publication  → publication-service
 *  - user.sync.queue.commentaire  → commentaire-service
 *  - user.sync.queue.reaction     → reaction-service
 *  - user.sync.queue.promo        → promo-service          ← NOUVEAU
 *  - user.sync.queue.subscription → subscription-service   ← NOUVEAU
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishUser(UserEventDTO dto) {
        // Publication vers publication-service
        publishToQueue(RabbitMQConfig.USER_QUEUE_PUBLICATION, dto);

        // Publication vers commentaire-service
        publishToQueue(RabbitMQConfig.USER_QUEUE_COMMENTAIRE, dto);

        // Publication vers reaction-service
        publishToQueue(RabbitMQConfig.USER_QUEUE_REACTION, dto);

        // Publication vers promo-service
        publishToQueue(RabbitMQConfig.USER_QUEUE_PROMO, dto);           // ← NOUVEAU

        // Publication vers subscription-service
        publishToQueue(RabbitMQConfig.USER_QUEUE_SUBSCRIPTION, dto);    // ← NOUVEAU

        publishToQueue(RabbitMQConfig.USER_QUEUE_PROJECT,     dto);
        publishToQueue(RabbitMQConfig.USER_QUEUE_APPLICATION, dto);
        publishToQueue(RabbitMQConfig.USER_QUEUE_SKILL,       dto);
    }

    private void publishToQueue(String queueName, UserEventDTO dto) {
        try {
            rabbitTemplate.convertAndSend(queueName, dto);
            log.info("[RabbitMQ] UserEventDTO publié dans '{}' pour userId={}",
                    queueName, dto.getId());
        } catch (AmqpException e) {
            log.error("[RabbitMQ] Échec de publication dans '{}' pour userId={} : {}",
                    queueName, dto.getId(), e.getMessage());
        }
    }
}