package com.madebyher.repository;

import com.madebyher.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Order> findAllByOrderByCreatedAtDesc();

    Optional<Order> findByOrderNumber(String orderNumber);


    // =========================================================
    // VILLAGE PARTNER ORDERS
    // =========================================================

    @Query("""
        SELECT DISTINCT o
        FROM Order o
        JOIN o.items oi
        JOIN oi.product p
        JOIN p.woman w
        WHERE w.partner.id = :partnerId
        ORDER BY o.createdAt DESC
    """)
    List<Order> findOrdersForPartner(
            @Param("partnerId") Long partnerId
    );
}