package com.esprit.publicationservice.messaging;

import com.esprit.publicationservice.config.RabbitMQConfig;
import com.esprit.publicationservice.dto.UserEventDTO;
import com.esprit.publicationservice.services.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Consommateur RabbitMQ côté publication-service.
 *
 * Écoute la queue "user.sync.queue" et met à jour le cache local
 * (UserCacheService) dès qu'un UserEventDTO est reçu de user-service.
 *
 * Spring AMQP désérialise automatiquement le JSON en UserEventDTO
 * grâce à Jackson2JsonMessageConverter configuré dans RabbitMQConfig.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserConsumer {

    private final UserCacheService userCacheService;

    /**
     * Réception d'un événement utilisateur depuis user-service.
     * Spring convertit automatiquement le message JSON en UserEventDTO.
     */
    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE,
            containerFactory = "rabbitListenerContainerFactory")
    public void receiveUser(UserEventDTO dto) {
        log.info("[RabbitMQ] UserEventDTO reçu : {}", dto);
        userCacheService.put(dto);
    }
}