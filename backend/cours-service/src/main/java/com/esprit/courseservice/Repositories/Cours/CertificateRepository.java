package com.esprit.courseservice.Repositories.Cours;

import com.esprit.courseservice.Entities.Cours.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, String> {
    Optional<Certificate> findByCourseIdAndUserId(Long courseId, Integer userId);
    List<Certificate> findByUserId(Integer userId);
    Optional<Certificate> findByVerificationCode(String verificationCode);
}
