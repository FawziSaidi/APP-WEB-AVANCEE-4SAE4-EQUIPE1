package com.esprit.projectservice.messaging;

import com.esprit.projectservice.config.RabbitMQConfig;
import com.esprit.projectservice.dto.UserEventDTO;
import com.esprit.projectservice.services.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor @Slf4j
public class UserConsumer {
    private final UserCacheService userCacheService;

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE_PROJECT,
            containerFactory = "rabbitListenerContainerFactory")
    public void receiveUser(UserEventDTO dto) {
        log.info("[RabbitMQ] User reçu dans project-service : {}", dto.getId());
        userCacheService.put(dto);
    }
}