package com.esprit.courseservice.dto.Cours;

import lombok.*;

public class CertificateDTO {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private String  id;
        private Integer userId;
        private String  userName;
        private Long    courseId;
        private String  courseTitle;
        private String  issuedAt;
        private String  url;
        private String  verificationCode;
    }
}
