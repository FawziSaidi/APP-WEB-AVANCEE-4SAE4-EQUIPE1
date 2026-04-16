package com.esprit.courseservice.Entities.Cours;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    // null = applies to all courses
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "discount_percent")
    private Integer discountPercent; // e.g. 20 = 20% off

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount; // flat amount off

    @Column(name = "max_uses")
    private Integer maxUses;

    @Builder.Default
    @Column(name = "times_used")
    private Integer timesUsed = 0;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Builder.Default
    @Column(name = "active")
    private Boolean active = true;
}
