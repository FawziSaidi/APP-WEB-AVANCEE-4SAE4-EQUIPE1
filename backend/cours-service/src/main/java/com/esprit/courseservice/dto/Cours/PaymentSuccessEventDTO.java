package com.esprit.courseservice.dto.Cours;

/**
 * DTO envoyé via RabbitMQ après un paiement réussi.
 */
public class PaymentSuccessEventDTO {

    private String paymentId;
    private Long courseId;
    private String courseTitle;
    private Integer userId;
    private String userName;
    private String userEmail;
    private Double amount;
    private String currency;
    private String paymentMethod;
    private String paidAt;

    public PaymentSuccessEventDTO() {}

    public PaymentSuccessEventDTO(String paymentId, Long courseId, String courseTitle,
                                  Integer userId, String userName, String userEmail,
                                  Double amount, String currency, String paymentMethod,
                                  String paidAt) {
        this.paymentId = paymentId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.paidAt = paidAt;
    }

    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

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

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaidAt() { return paidAt; }
    public void setPaidAt(String paidAt) { this.paidAt = paidAt; }

    @Override
    public String toString() {
        return "PaymentSuccessEventDTO{paymentId=" + paymentId +
                ", courseId=" + courseId +
                ", userId=" + userId +
                ", amount=" + amount + "}";
    }
}