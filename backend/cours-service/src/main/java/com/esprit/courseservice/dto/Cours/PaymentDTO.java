package com.esprit.courseservice.dto.Cours;

import lombok.*;

public class PaymentDTO {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Request {
        private Integer userId;
        private Long    courseId;
        private String  method;       // "CARD" | "PAYPAL" | "BANK_TRANSFER"
        private String  cardToken;
        private String  couponCode;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private String paymentId;
        private String status;
        private double amount;
        private String currency;
        private String enrolledAt;
        private String receiptUrl;
        private String errorMessage;
    }
}
