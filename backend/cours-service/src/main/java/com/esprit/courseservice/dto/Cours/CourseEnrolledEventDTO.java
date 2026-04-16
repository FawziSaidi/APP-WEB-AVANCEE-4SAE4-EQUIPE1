package com.esprit.courseservice.dto.Cours;

/**
 * DTO envoyé via RabbitMQ vers user-service et notification-service.
 * Contient les informations d'inscription à un cours.
 */
public class CourseEnrolledEventDTO {

    private Long enrollmentId;
    private Long courseId;
    private String courseTitle;
    private Integer userId;
    private String userName;
    private String userEmail;
    private String enrolledAt;
    private Double pricePaid;

    public CourseEnrolledEventDTO() {}

    public CourseEnrolledEventDTO(Long enrollmentId, Long courseId, String courseTitle,
                                  Integer userId, String userName, String userEmail,
                                  String enrolledAt, Double pricePaid) {
        this.enrollmentId = enrollmentId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.enrolledAt = enrolledAt;
        this.pricePaid = pricePaid;
    }

    // Getters and Setters
    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }

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

    public String getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(String enrolledAt) { this.enrolledAt = enrolledAt; }

    public Double getPricePaid() { return pricePaid; }
    public void setPricePaid(Double pricePaid) { this.pricePaid = pricePaid; }

    @Override
    public String toString() {
        return "CourseEnrolledEventDTO{courseId=" + courseId +
                ", userId=" + userId +
                ", courseTitle='" + courseTitle + "'}";
    }
}