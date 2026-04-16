package com.esprit.lessonservice.dto.Cours;

import lombok.*;

public class ProgressDTO {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SubmitRequest {
        private Integer userId;
        private Long    courseId;
        private Long    lessonId;
        private String  completedAt;       // ISO string from frontend e.g. "2024-01-15T10:30:00.000Z"
        private Integer timeSpentSeconds;
        private String  notes;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SubmitResponse {
        private boolean success;
        private int     progressPercent;
        private boolean certificateUnlocked;
        private String  message;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CourseProgress {
        private Long    courseId;
        private String  courseTitle;
        private Integer userId;
        private int     completedLessons;
        private int     totalLessons;
        private int     progressPercent;
        private String  lastActivity;
        private boolean certificateUnlocked;
        private String  certificateUrl;
    }
}
