package com.esprit.courseservice.dto.Cours;

import com.esprit.courseservice.Entities.Cours.Course;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CourseDTO {

    @Getter @Setter
    public static class Request {
        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Description is required")
        private String description;

        @NotBlank(message = "Category is required")
        private String category;

        @NotNull(message = "Level is required")
        private Course.Level level;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price must be positive")
        private BigDecimal price;

        private String thumbnail;
        private Course.Status status = Course.Status.DRAFT;

        // ── NEW ──────────────────────────────────────────────
        private String       prerequisites;
        private List<String> objectives;
        private List<String> tags;
        private Integer      enrollmentLimit;
        private Boolean      discussionsEnabled;
        private Boolean      reviewsEnabled;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long          id;
        private String        title;
        private String        description;
        private String        category;
        private Course.Level  level;
        private BigDecimal    price;
        private Course.Status status;
        private String        thumbnail;
        private int           lessonCount;
        private int           enrollmentCount;
        private LocalDateTime createdAt;

        // ── NEW ──────────────────────────────────────────────
        private String       prerequisites;
        private List<String> objectives;
        private List<String> tags;
        private Double       rating;
        private Integer      ratingCount;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Summary {
        private Long          id;
        private String        title;
        private String        category;
        private Course.Level  level;
        private BigDecimal    price;
        private Course.Status status;
        private String        thumbnail;
        private int           lessonCount;
        private int           enrollmentCount;  // NEW
        private Double        rating;           // NEW
        private Integer       ratingCount;      // NEW
    }
}