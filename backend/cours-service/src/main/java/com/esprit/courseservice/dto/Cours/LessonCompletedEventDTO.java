package com.esprit.courseservice.dto.Cours;

/**
 * DTO reçu via RabbitMQ depuis lesson-service.
 * Contient les informations d'une leçon complétée par un utilisateur.
 */
public class LessonCompletedEventDTO {

    private Long lessonId;
    private Long courseId;
    private Integer userId;
    private String lessonTitle;
    private String courseTitle;
    private Integer timeSpentSeconds;
    private String completedAt;

    // Default constructor (required for Jackson deserialization)
    public LessonCompletedEventDTO() {}

    // Parameterized constructor
    public LessonCompletedEventDTO(Long lessonId, Long courseId, Integer userId,
                                   String lessonTitle, String courseTitle,
                                   Integer timeSpentSeconds, String completedAt) {
        this.lessonId = lessonId;
        this.courseId = courseId;
        this.userId = userId;
        this.lessonTitle = lessonTitle;
        this.courseTitle = courseTitle;
        this.timeSpentSeconds = timeSpentSeconds;
        this.completedAt = completedAt;
    }

    // Getters
    public Long getLessonId() {
        return lessonId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    // Setters
    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public String toString() {
        return "LessonCompletedEventDTO{" +
                "lessonId=" + lessonId +
                ", courseId=" + courseId +
                ", userId=" + userId +
                ", lessonTitle='" + lessonTitle + '\'' +
                ", courseTitle='" + courseTitle + '\'' +
                ", timeSpentSeconds=" + timeSpentSeconds +
                ", completedAt='" + completedAt + '\'' +
                '}';
    }
}