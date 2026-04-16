package com.esprit.courseservice.events.publishers;

import com.esprit.courseservice.dto.Cours.CourseCompletedEventDTO;
import com.esprit.courseservice.dto.Cours.CourseEnrolledEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.course:course.exchange}")
    private String courseExchange;

    @Value("${rabbitmq.routing-key.course.enrolled:course.enrolled}")
    private String courseEnrolledRoutingKey;

    @Value("${rabbitmq.routing-key.course.completed:course.completed}")
    private String courseCompletedRoutingKey;

    public void publishCourseEnrolled(CourseEnrolledEventDTO event) {
        try {
            rabbitTemplate.convertAndSend(courseExchange, courseEnrolledRoutingKey, event);
            log.info("📤 Course enrolled event published: User {} enrolled in course {}",
                    event.getUserId(), event.getCourseId());
        } catch (Exception e) {
            log.error("❌ Failed to publish course enrolled event: {}", e.getMessage());
        }
    }

    public void publishCourseCompleted(CourseCompletedEventDTO event) {
        try {
            rabbitTemplate.convertAndSend(courseExchange, courseCompletedRoutingKey, event);
            log.info("📤 Course completed event published: User {} completed course {}",
                    event.getUserId(), event.getCourseId());
        } catch (Exception e) {
            log.error("❌ Failed to publish course completed event: {}", e.getMessage());
        }
    }
}