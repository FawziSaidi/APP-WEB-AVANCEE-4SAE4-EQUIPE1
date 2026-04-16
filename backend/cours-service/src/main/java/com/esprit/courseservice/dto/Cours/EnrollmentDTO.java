package com.esprit.courseservice.dto.Cours;

import lombok.*;
import java.time.LocalDateTime;

public class EnrollmentDTO {

    @Getter @Setter @Builder
    public static class Response {
        private Long id;
        private Long courseId;
        private String courseTitle;
        private String courseThumbnail;
        private int totalLessons;
        private int completedLessons;
        private int progressPercent;
        private LocalDateTime enrolledAt;
    }
}