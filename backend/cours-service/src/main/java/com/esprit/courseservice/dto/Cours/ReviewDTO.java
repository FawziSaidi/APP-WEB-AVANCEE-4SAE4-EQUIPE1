package com.esprit.courseservice.dto.Cours;

import lombok.*;

public class ReviewDTO {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Request {
        private Integer userId;
        private Long    courseId;
        private int     rating;
        private String  comment;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long    id;
        private Integer userId;
        private String  userName;
        private Long    courseId;
        private int     rating;
        private String  comment;
        private String  createdAt;
        private int     helpful;
    }
}
