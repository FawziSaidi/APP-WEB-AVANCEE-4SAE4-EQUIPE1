package com.esprit.courseservice.Entities.Cours;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "certificates",
        uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Certificate {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @JoinColumn(name = "user_id", nullable = false)
    private Integer userId;

    @Builder.Default
    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt = LocalDateTime.now();

    @Column(name = "verification_code", unique = true, nullable = false)
    private String verificationCode;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.verificationCode == null) {
            this.verificationCode = UUID.randomUUID().toString()
                    .replace("-", "").substring(0, 8).toUpperCase();
        }
    }
}
