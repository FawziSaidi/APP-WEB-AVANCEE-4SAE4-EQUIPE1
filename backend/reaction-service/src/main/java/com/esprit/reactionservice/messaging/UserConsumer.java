package com.esprit.reactionservice.messaging;

import com.esprit.reactionservice.config.RabbitMQConfig;
import com.esprit.reactionservice.dto.UserEventDTO;
import com.esprit.reactionservice.services.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Consommateur RabbitMQ côté reaction-service.
 *
 * Écoute la queue "user.sync.queue.reaction" et met à jour
 * le cache local (UserCacheService) dès qu'un UserEventDTO
 * est reçu de user-service.
 *
 * Spring AMQP désérialise automatiquement le JSON en UserEventDTO
 * grâce à Jackson2JsonMessageConverter configuré dans RabbitMQConfig.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserConsumer {

    private final UserCacheService userCacheService;

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE_REACTION,
            containerFactory = "rabbitListenerContainerFactory")
    public void receiveUser(UserEventDTO dto) {
        log.info("[RabbitMQ] UserEventDTO reçu dans reaction-service : {}", dto);
        userCacheService.put(dto);
    }
}