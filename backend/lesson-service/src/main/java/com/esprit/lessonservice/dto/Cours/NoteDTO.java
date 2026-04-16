package com.esprit.lessonservice.dto.Cours;

import lombok.*;

public class NoteDTO {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Request {
        private Integer userId;
        private String  content;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long    id;
        private Long    lessonId;
        private String  lessonTitle;
        private Long    courseId;
        private String  courseTitle;
        private Integer userId;
        private String  content;
        private String  updatedAt;
    }
}
