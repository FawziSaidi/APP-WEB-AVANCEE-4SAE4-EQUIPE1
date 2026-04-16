package com.esprit.lessonservice.events.publishers;

import com.esprit.lessonservice.dto.Cours.LessonCompletedEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LessonEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.lesson:lesson.exchange}")
    private String lessonExchange;

    @Value("${rabbitmq.routing-key.lesson.completed:lesson.completed}")
    private String lessonCompletedRoutingKey;

    public void publishLessonCompleted(LessonCompletedEventDTO event) {
        try {
            rabbitTemplate.convertAndSend(lessonExchange, lessonCompletedRoutingKey, event);
            log.info("📤 Lesson completed event published: Lesson {} by user {}",
                    event.getLessonId(), event.getUserId());
        } catch (Exception e) {
            log.error("❌ Failed to publish lesson completed event: {}", e.getMessage());
        }
    }
}