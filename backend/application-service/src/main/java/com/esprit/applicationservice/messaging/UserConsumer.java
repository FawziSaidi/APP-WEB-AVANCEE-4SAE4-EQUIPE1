package com.esprit.applicationservice.messaging;

import com.esprit.applicationservice.config.RabbitMQConfig;
import com.esprit.applicationservice.dto.UserEventDTO;
import com.esprit.applicationservice.services.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserConsumer {

    private final UserCacheService userCacheService;

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE_APPLICATION,
            containerFactory = "rabbitListenerContainerFactory")
    public void receiveUser(UserEventDTO dto) {
        log.info("[RabbitMQ] User reçu dans application-service : {}", dto);
        userCacheService.put(dto);
    }
}