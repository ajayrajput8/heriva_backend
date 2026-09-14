package com.madebyher.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="cart_items", uniqueConstraints=@UniqueConstraint(columnNames={"user_id","product_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private User user;

    @ManyToOne(optional=false)
    private Product product;

    private Integer quantity;
}
