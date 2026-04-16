package com.esprit.lessonservice.Repositories.Cours;

import com.esprit.lessonservice.Entities.Cours.LessonBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonBookmarkRepository extends JpaRepository<LessonBookmark, Long> {
    boolean existsByLessonIdAndUserId(Long lessonId, Integer userId);
    void deleteByLessonIdAndUserId(Long lessonId, Integer userId);
    List<LessonBookmark> findByUserId(Integer userId);
}
