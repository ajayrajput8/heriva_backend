package com.madebyher.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="village_partners")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VillagePartner {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional=false)
    @JoinColumn(name="user_id", unique=true)
    private User user;

    private String village;
    private String district;
    private String state;
    private String pincode;
    private String aadhaarLast4;
    private String bio;

    @Enumerated(EnumType.STRING)
    private PartnerStatus status = PartnerStatus.PENDING;

    private LocalDateTime approvedAt;
    private LocalDateTime createdAt = LocalDateTime.now();
}
