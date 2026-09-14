package com.madebyher.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(length=5000)
    private String description;

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal price;

    private Integer stockQuantity;
    private String mainImageUrl;

    @ElementCollection
    @CollectionTable(name="product_images", joinColumns=@JoinColumn(name="product_id"))
    @Column(name="image_url")
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    private Double weightKg;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.DRAFT;

    @ManyToOne(optional=false)
    private Category category;

    @ManyToOne(optional=false)
    private Woman woman;

    private boolean featured = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
