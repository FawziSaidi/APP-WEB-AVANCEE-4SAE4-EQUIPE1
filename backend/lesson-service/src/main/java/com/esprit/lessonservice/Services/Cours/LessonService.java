package com.esprit.lessonservice.Services.Cours;

import com.esprit.lessonservice.client.CourseClient;
import com.esprit.lessonservice.dto.Cours.LessonDTO;
import com.esprit.lessonservice.dto.Cours.ReorderDTO;
import com.esprit.lessonservice.dto.Cours.LessonCompletedEventDTO;
import com.esprit.lessonservice.Entities.Cours.Lesson;
import com.esprit.lessonservice.Entities.Cours.LessonProgress;
import com.esprit.lessonservice.Repositories.Cours.LessonProgressRepository;
import com.esprit.lessonservice.Repositories.Cours.LessonRepository;
import com.esprit.lessonservice.events.publishers.LessonEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonProgressRepository progressRepository;
    private final CourseClient courseClient;
    private final LessonEventPublisher eventPublisher;  // ← ADDED: RabbitMQ publisher

    public LessonDTO.Response addLesson(Long courseId, LessonDTO.Request request) {
        // Verify course exists via Feign
        try {
            courseClient.getCourse(courseId);
        } catch (Exception e) {
            throw new RuntimeException("Course not found with id: " + courseId);
        }

        Lesson lesson = Lesson.builder()
                .courseId(courseId)
                .title(request.getTitle())
                .description(request.getDescription())
                .contentUrl(request.getContentUrl())
                .duration(request.getDuration())
                .orderIndex(request.getOrderIndex())
                .build();
        return toResponse(lessonRepository.save(lesson), false);
    }

    public LessonDTO.Response updateLesson(Long lessonId, LessonDTO.Request request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setContentUrl(request.getContentUrl());
        lesson.setDuration(request.getDuration());
        lesson.setOrderIndex(request.getOrderIndex());
        return toResponse(lessonRepository.save(lesson), false);
    }

    public void deleteLesson(Long lessonId) {
        lessonRepository.deleteById(lessonId);
    }

    // Returns lessons with completed status for the requesting user
    public List<LessonDTO.Response> getLessonsForFreelancer(Long courseId, Integer userId) {
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByOrderIndexAsc(courseId);

        // Get enrollment via Feign
        CourseClient.EnrollmentDTO enrollment = null;
        try {
            enrollment = courseClient.getEnrollment(courseId, userId);
        } catch (Exception e) {
            // User not enrolled
        }

        // Get completed lesson IDs for this user's enrollment
        final Map<Long, Boolean> completedMap;

        if (enrollment != null) {
            final Long enrollmentId = enrollment.id();
            List<LessonProgress> progresses = progressRepository.findByEnrollmentId(enrollmentId);
            completedMap = progresses.stream()
                    .collect(Collectors.toMap(
                            p -> p.getLesson().getId(),
                            LessonProgress::getCompleted
                    ));
        } else {
            completedMap = Map.of();
        }

        return lessons.stream()
                .map(l -> toResponse(l, completedMap.getOrDefault(l.getId(), false)))
                .collect(Collectors.toList());
    }

    public List<LessonDTO.Response> getLessonsForAdmin(Long courseId) {
        return lessonRepository.findByCourseIdOrderByOrderIndexAsc(courseId)
                .stream().map(l -> toResponse(l, false))
                .collect(Collectors.toList());
    }

    @Transactional
    public void reorderLessons(Long courseId, ReorderDTO.Request request) {
        request.getLessons().forEach(item -> {
            Lesson lesson = lessonRepository.findById(item.getId())
                    .orElseThrow(() -> new RuntimeException("Lesson not found: " + item.getId()));
            if (!lesson.getCourseId().equals(courseId)) {
                throw new RuntimeException("Lesson does not belong to this course");
            }
            lesson.setOrderIndex(item.getOrderIndex());
            lessonRepository.save(lesson);
        });
    }

    // =========================================================================
    // LESSON COMPLETION WITH RABBITMQ EVENT
    // =========================================================================

    @Transactional
    public void completeLesson(Long lessonId, Integer userId) {
        // Get the lesson
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + lessonId));

        // Get enrollment via Feign
        CourseClient.EnrollmentDTO enrollment = null;
        try {
            enrollment = courseClient.getEnrollment(lesson.getCourseId(), userId);
        } catch (Exception e) {
            throw new RuntimeException("User not enrolled in this course: " + e.getMessage());
        }

        if (enrollment == null) {
            throw new RuntimeException("User not enrolled in this course");
        }

        // Check if already completed
        final Long enrollmentId = enrollment.id();
        LessonProgress existingProgress = progressRepository
                .findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElse(null);

        if (existingProgress != null && existingProgress.getCompleted()) {
            throw new RuntimeException("Lesson already completed by this user");
        }

        // Save or update progress
        LessonProgress progress;
        if (existingProgress != null) {
            progress = existingProgress;
        } else {
            progress = LessonProgress.builder()
                    .enrollmentId(enrollmentId)
                    .lesson(lesson)
                    .completed(false)
                    .build();
        }

        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        progressRepository.save(progress);

        // Get course title for the event (via Feign)
        String courseTitle = "Unknown Course";
        try {
            CourseClient.CourseDTO course = courseClient.getCourse(lesson.getCourseId());
            courseTitle = course.title();
        } catch (Exception e) {
            // Course title not available, use default
        }

        // 🚀 PUBLISH RABBITMQ EVENT
        LessonCompletedEventDTO event = new LessonCompletedEventDTO(
                lessonId,
                lesson.getCourseId(),
                userId,
                lesson.getTitle(),
                courseTitle,
                0, // timeSpentSeconds - can be calculated if needed
                LocalDateTime.now().toString()
        );
        eventPublisher.publishLessonCompleted(event);
    }

    private LessonDTO.Response toResponse(Lesson l, boolean completed) {
        return LessonDTO.Response.builder()
                .id(l.getId())
                .courseId(l.getCourseId())
                .title(l.getTitle())
                .description(l.getDescription())
                .contentUrl(l.getContentUrl())
                .duration(l.getDuration())
                .orderIndex(l.getOrderIndex())
                .completed(completed)
                .build();
    }
}