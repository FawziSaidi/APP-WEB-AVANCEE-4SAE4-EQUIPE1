package com.esprit.courseservice.dto.Cours;

import lombok.*;

public class CouponDTO {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ValidateResponse {
        private boolean valid;
        private Integer discountPercent;
        private Double  discountAmount;
        private Double  finalPrice;
        private String  errorMessage;
    }
}
