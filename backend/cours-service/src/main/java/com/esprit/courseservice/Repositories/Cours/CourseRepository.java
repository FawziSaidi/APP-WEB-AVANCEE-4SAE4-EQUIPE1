package com.esprit.courseservice.Repositories.Cours;

import com.esprit.courseservice.Entities.Cours.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByStatus(Course.Status status);
    List<Course> findByCategoryAndStatus(String category, Course.Status status);
    List<Course> findByCreatedBy(Integer admin);
}