package com.esprit.lessonservice.dto.Cours;

import jakarta.validation.constraints.*;
import lombok.*;

public class LessonDTO {

    @Getter @Setter
    public static class Request {
        @NotBlank(message = "Title is required")
        private String title;

        private String description;

        @NotBlank(message = "Content URL is required")
        private String contentUrl;

        @Min(value = 0, message = "Duration must be positive")
        private Integer duration = 0;

        private Integer orderIndex = 0;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long    id;
        private Long    courseId;
        private String  title;
        private String  description;
        private String  contentUrl;
        private Integer duration;
        private Integer orderIndex;
        private boolean completed;
        private String  completedAt;  // NEW — ISO timestamp

        // NEW — user-specific fields (null for non-enrolled / admin view)
        private String  notes;
        private boolean bookmarked;
    }
}