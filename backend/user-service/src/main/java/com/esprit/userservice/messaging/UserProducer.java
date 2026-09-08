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
 * Publie un UserEventDTO dans toutes les queues à chaque
 * création ou modification d'un utilisateur.
 *
 * Queues cibles :
 *  Existantes :
 *   - user.sync.queue.publication  → publication-service
 *   - user.sync.queue.commentaire  → commentaire-service
 *   - user.sync.queue.reaction     → reaction-service
 *   - user.sync.queue.promo        → promo-service
 *   - user.sync.queue.subscription → subscription-service
 *   - user.sync.queue.project      → project-service
 *   - user.sync.queue.application  → application-service
 *   - user.sync.queue.skill        → skill-service
 *  Nouvelles :
 *   - inscription.user.queue       → inscription-service
 *   - event-user.queue             → event-service
 *   - activity-user.queue          → activity-service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishUser(UserEventDTO dto) {
        // ── Queues existantes ────────────────────────────────────────────
        publishToQueue(RabbitMQConfig.USER_QUEUE_PUBLICATION,  dto);
        publishToQueue(RabbitMQConfig.USER_QUEUE_COMMENTAIRE,  dto);
        publishToQueue(RabbitMQConfig.USER_QUEUE_REACTION,     dto);
        publishToQueue(RabbitMQConfig.USER_QUEUE_PROMO,        dto);
        publishToQueue(RabbitMQConfig.USER_QUEUE_SUBSCRIPTION, dto);
        publishToQueue(RabbitMQConfig.USER_QUEUE_PROJECT,      dto);
        publishToQueue(RabbitMQConfig.USER_QUEUE_APPLICATION,  dto);
        publishToQueue(RabbitMQConfig.USER_QUEUE_SKILL,        dto);

        // ── Nouvelles queues ─────────────────────────────────────────────
        publishToQueue(RabbitMQConfig.USER_QUEUE_INSCRIPTION,  dto);
        publishToQueue(RabbitMQConfig.USER_QUEUE_EVENT,        dto);
        publishToQueue(RabbitMQConfig.USER_QUEUE_ACTIVITY,     dto);
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