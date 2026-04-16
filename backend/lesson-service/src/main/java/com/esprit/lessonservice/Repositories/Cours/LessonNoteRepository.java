package com.esprit.lessonservice.Repositories.Cours;

import com.esprit.lessonservice.Entities.Cours.LessonNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonNoteRepository extends JpaRepository<LessonNote, Long> {
    Optional<LessonNote> findByLessonIdAndUserId(Long lessonId, Integer userId);
    List<LessonNote> findByUserId(Integer userId);
}
