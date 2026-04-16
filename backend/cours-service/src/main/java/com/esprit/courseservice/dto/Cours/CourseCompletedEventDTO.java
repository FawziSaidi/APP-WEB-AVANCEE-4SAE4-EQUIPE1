package com.esprit.courseservice.dto.Cours;

/**
 * DTO envoyé via RabbitMQ vers certificate-service et analytics-service.
 * Contient les informations de complétion d'un cours.
 */
public class CourseCompletedEventDTO {

    private Long courseId;
    private String courseTitle;
    private Integer userId;
    private String userName;
    private String userEmail;
    private String completedAt;
    private Integer totalLessons;
    private Integer totalTimeSpentMinutes;
    private String certificateUrl;
    private Double finalGrade;

    public CourseCompletedEventDTO() {}

    public CourseCompletedEventDTO(Long courseId, String courseTitle, Integer userId,
                                   String userName, String userEmail, String completedAt,
                                   Integer totalLessons, Integer totalTimeSpentMinutes,
                                   String certificateUrl, Double finalGrade) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.completedAt = completedAt;
        this.totalLessons = totalLessons;
        this.totalTimeSpentMinutes = totalTimeSpentMinutes;
        this.certificateUrl = certificateUrl;
        this.finalGrade = finalGrade;
    }

    // Getters and Setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public Integer getTotalLessons() { return totalLessons; }
    public void setTotalLessons(Integer totalLessons) { this.totalLessons = totalLessons; }

    public Integer getTotalTimeSpentMinutes() { return totalTimeSpentMinutes; }
    public void setTotalTimeSpentMinutes(Integer totalTimeSpentMinutes) { this.totalTimeSpentMinutes = totalTimeSpentMinutes; }

    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }

    public Double getFinalGrade() { return finalGrade; }
    public void setFinalGrade(Double finalGrade) { this.finalGrade = finalGrade; }

    @Override
    public String toString() {
        return "CourseCompletedEventDTO{courseId=" + courseId +
                ", userId=" + userId +
                ", courseTitle='" + courseTitle + "'}";
    }
}