package com.madebyher.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="reviews", uniqueConstraints=@UniqueConstraint(columnNames={"customer_id","product_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private User customer;

    @ManyToOne(optional=false)
    private Product product;

    private Integer rating;

    @Column(length=2000)
    private String comment;

    private boolean approved = true;
    private LocalDateTime createdAt = LocalDateTime.now();
}
