package com.esprit.skillservice.messaging;

import com.esprit.skillservice.config.RabbitMQConfig;
import com.esprit.skillservice.dto.UserEventDTO;
import com.esprit.skillservice.services.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserConsumer {

    private final UserCacheService userCacheService;

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE_SKILL,
            containerFactory = "rabbitListenerContainerFactory")
    public void receiveUser(UserEventDTO dto) {
        log.info("[RabbitMQ] User reçu dans skill-service : {}", dto);
        userCacheService.put(dto);
    }
}