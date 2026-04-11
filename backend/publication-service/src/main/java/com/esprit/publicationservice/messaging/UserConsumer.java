package com.esprit.publicationservice.messaging;

import com.esprit.publicationservice.config.RabbitMQConfig;
import com.esprit.publicationservice.dto.UserEventDTO;
import com.esprit.publicationservice.services.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserConsumer {

    private final UserCacheService userCacheService;

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE_PUBLICATION,
            containerFactory = "rabbitListenerContainerFactory")
    public void receiveUser(UserEventDTO dto) {
        log.info("[RabbitMQ] UserEventDTO reçu : {}", dto);
        userCacheService.put(dto);
    }
}