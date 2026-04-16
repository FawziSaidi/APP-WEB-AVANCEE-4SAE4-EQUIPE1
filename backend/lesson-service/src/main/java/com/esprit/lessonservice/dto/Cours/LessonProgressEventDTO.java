package com.esprit.lessonservice.dto.Cours;

/**
 * DTO envoyé via RabbitMQ pour suivre la progression globale.
 */
public class LessonProgressEventDTO {

    private Long enrollmentId;
    private Long courseId;
    private Integer userId;
    private Integer completedLessons;
    private Integer totalLessons;
    private Integer progressPercent;
    private String lastAccessedAt;

    public LessonProgressEventDTO() {}

    public LessonProgressEventDTO(Long enrollmentId, Long courseId, Integer userId,
                                  Integer completedLessons, Integer totalLessons,
                                  Integer progressPercent, String lastAccessedAt) {
        this.enrollmentId = enrollmentId;
        this.courseId = courseId;
        this.userId = userId;
        this.completedLessons = completedLessons;
        this.totalLessons = totalLessons;
        this.progressPercent = progressPercent;
        this.lastAccessedAt = lastAccessedAt;
    }

    // Getters and Setters
    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getCompletedLessons() { return completedLessons; }
    public void setCompletedLessons(Integer completedLessons) { this.completedLessons = completedLessons; }

    public Integer getTotalLessons() { return totalLessons; }
    public void setTotalLessons(Integer totalLessons) { this.totalLessons = totalLessons; }

    public Integer getProgressPercent() { return progressPercent; }
    public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }

    public String getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(String lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }

    @Override
    public String toString() {
        return "LessonProgressEventDTO{courseId=" + courseId +
                ", userId=" + userId +
                ", progress=" + progressPercent + "%}";
    }
}