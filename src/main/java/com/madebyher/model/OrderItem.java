package com.madebyher.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JsonBackReference
    private Order order;

    @ManyToOne(optional = false)
    private Product product;

    @ManyToOne(optional = false)
    private Woman woman;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal lineTotal;

    // Example platform split. Replace with your final business rules.
    private BigDecimal womanEarning;

    private BigDecimal partnerCommission;

    private BigDecimal platformCommission;
}