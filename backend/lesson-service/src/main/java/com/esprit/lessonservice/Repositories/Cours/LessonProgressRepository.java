package com.esprit.lessonservice.Repositories.Cours;

import com.esprit.lessonservice.Entities.Cours.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    List<LessonProgress> findByEnrollmentId(Long enrollmentId);

    Optional<LessonProgress> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);

    @Query("SELECT COUNT(lp) FROM LessonProgress lp WHERE lp.enrollmentId = :enrollmentId AND lp.completed = true")
    int countCompletedByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    @Query("""
        SELECT lp FROM LessonProgress lp
        JOIN FETCH lp.lesson l
        WHERE lp.completed = true
          AND (:courseId IS NULL OR l.courseId = :courseId)
          AND (:enrollmentId IS NULL OR lp.enrollmentId = :enrollmentId)
          AND (:dateFrom IS NULL OR lp.completedAt >= :dateFrom)
          AND (:dateTo IS NULL OR lp.completedAt <= :dateTo)
        ORDER BY lp.completedAt DESC
        """)
    List<LessonProgress> findAllCompleted(
            @Param("courseId") Long courseId,
            @Param("enrollmentId") Long enrollmentId,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo
    );
}