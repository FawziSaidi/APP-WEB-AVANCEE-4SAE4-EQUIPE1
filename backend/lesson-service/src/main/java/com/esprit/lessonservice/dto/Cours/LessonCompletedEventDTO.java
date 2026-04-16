package com.esprit.lessonservice.dto.Cours;

/**
 * DTO envoyé via RabbitMQ vers course-service et d'autres services.
 * Contient les informations nécessaires pour mettre à jour la progression
 * d'un cours lorsqu'une leçon est terminée.
 */
public class LessonCompletedEventDTO {

    private Long lessonId;
    private Long courseId;
    private Integer userId;
    private String lessonTitle;
    private String courseTitle;
    private Integer timeSpentSeconds;
    private String completedAt;

    public LessonCompletedEventDTO() {}

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

    // Getters and Setters
    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getLessonTitle() { return lessonTitle; }
    public void setLessonTitle(String lessonTitle) { this.lessonTitle = lessonTitle; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public Integer getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(Integer timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    @Override
    public String toString() {
        return "LessonCompletedEventDTO{lessonId=" + lessonId +
                ", courseId=" + courseId +
                ", userId=" + userId +
                ", lessonTitle='" + lessonTitle + "'}";
    }
}