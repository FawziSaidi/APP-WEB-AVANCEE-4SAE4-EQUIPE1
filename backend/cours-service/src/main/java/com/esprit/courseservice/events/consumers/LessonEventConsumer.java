package com.esprit.courseservice.events.consumers;

import com.esprit.courseservice.dto.Cours.LessonCompletedEventDTO;  // ← Local DTO
import com.esprit.courseservice.Services.Cours.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LessonEventConsumer {

    private final CourseService courseService;

    @RabbitListener(queues = "${rabbitmq.queue.lesson.completed:lesson.completed.queue}")
    public void handleLessonCompleted(LessonCompletedEventDTO event) {
        log.info("📥 Lesson completed event received: Lesson {} by user {}",
                event.getLessonId(), event.getUserId());

        // Update course progress
        courseService.updateProgress(event.getCourseId(), event.getUserId());
    }
}