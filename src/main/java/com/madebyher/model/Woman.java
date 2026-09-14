package com.madebyher.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="women")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Woman {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    private String phone;
    private String village;
    private String district;
    private String state;
    private String photoUrl;
    private String story;
    private String skills;
    private boolean active = true;

    @ManyToOne(optional=false)
    @JoinColumn(name="partner_id")
    private VillagePartner partner;

    private LocalDateTime createdAt = LocalDateTime.now();
}
