package com.esprit.courseservice.Entities.Cours;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DRAFT;

    private String thumbnail;

    @JoinColumn(name = "created_by", nullable = false)
    private Integer createdBy;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ── NEW FIELDS ────────────────────────────────────────────────────────────

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "course_tags", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Column(name = "prerequisites", columnDefinition = "TEXT")
    private String prerequisites;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "course_objectives", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "objective")
    private List<String> objectives = new ArrayList<>();

    @Column(name = "enrollment_limit")
    private Integer enrollmentLimit;

    @Builder.Default
    @Column(name = "discussions_enabled")
    private Boolean discussionsEnabled = true;

    @Builder.Default
    @Column(name = "reviews_enabled")
    private Boolean reviewsEnabled = true;

    // ── EXISTING RELATIONS ────────────────────────────────────────────────────


    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Status { DRAFT, PUBLISHED }
    public enum Level  { BEGINNER, INTERMEDIATE, ADVANCED }
}