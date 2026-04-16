package com.esprit.lessonservice.dto.Cours;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

public class ReorderDTO {

    @Getter @Setter
    public static class Request {
        @NotNull
        private List<LessonOrder> lessons;

        @Getter @Setter
        public static class LessonOrder {
            private Long id;
            private Integer orderIndex;
        }
    }
}