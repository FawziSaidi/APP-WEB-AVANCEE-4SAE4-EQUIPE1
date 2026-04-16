package com.esprit.lessonservice.Controllers;

import com.esprit.lessonservice.client.UserClient;
import com.esprit.lessonservice.dto.Cours.*;
import com.esprit.lessonservice.Entities.Cours.LessonProgress;
import com.esprit.lessonservice.Services.Cours.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;
    private final UserClient userClient;

    // =========================================================================
    // LESSON CRUD & REORDERING
    // =========================================================================

    @GetMapping("/api/courses/{courseId}/lessons")
    public ResponseEntity<List<LessonDTO.Response>> getLessons(
            @PathVariable Long courseId,
            @RequestParam(required = false) Integer userId) {
        if (userId != null) {
            return ResponseEntity.ok(lessonService.getLessonsForFreelancer(courseId, userId));
        }
        return ResponseEntity.ok(lessonService.getLessonsForAdmin(courseId));
    }

    @PostMapping("/api/courses/{courseId}/lessons")
    public ResponseEntity<LessonDTO.Response> addLesson(
            @PathVariable Long courseId,
            @Valid @RequestBody LessonDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lessonService.addLesson(courseId, request));
    }

    @PutMapping("/api/lessons/{lessonId}")
    public ResponseEntity<LessonDTO.Response> updateLesson(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonDTO.Request request) {
        return ResponseEntity.ok(lessonService.updateLesson(lessonId, request));
    }

    @DeleteMapping("/api/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/courses/{courseId}/lessons/reorder")
    public ResponseEntity<Void> reorderLessons(
            @PathVariable Long courseId,
            @Valid @RequestBody ReorderDTO.Request request) {
        lessonService.reorderLessons(courseId, request);
        return ResponseEntity.ok().build();
    }

    // =========================================================================
    // TODO: Add progress, bookmarks, notes endpoints here
    // =========================================================================
}