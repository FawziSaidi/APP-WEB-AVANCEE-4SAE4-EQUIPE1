package com.esprit.commentaireservice.messaging;

import com.esprit.commentaireservice.config.RabbitMQConfig;
import com.esprit.commentaireservice.dto.UserEventDTO;
import com.esprit.commentaireservice.services.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Consommateur RabbitMQ côté commentaire-service.
 *
 * Écoute la queue "user.sync.queue.commentaire" et met à jour
 * le cache local dès qu'un UserEventDTO est reçu de user-service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserConsumer {

    private final UserCacheService userCacheService;

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE_COMMENTAIRE,
            containerFactory = "rabbitListenerContainerFactory")
    public void receiveUser(UserEventDTO dto) {
        log.info("[RabbitMQ] UserEventDTO reçu dans commentaire-service : {}", dto);
        userCacheService.put(dto);
    }
}