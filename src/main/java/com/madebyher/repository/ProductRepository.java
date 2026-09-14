package com.madebyher.repository;

import com.madebyher.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // =========================================================
    // PUBLIC MARKETPLACE
    // =========================================================

    Page<Product> findByStatus(
            ProductStatus status,
            Pageable pageable
    );

    Page<Product> findByStatusAndNameContainingIgnoreCase(
            ProductStatus status,
            String name,
            Pageable pageable
    );

    Page<Product> findByStatusAndCategoryId(
            ProductStatus status,
            Long categoryId,
            Pageable pageable
    );


    // =========================================================
    // WOMAN'S PRODUCTS
    // =========================================================

    List<Product> findByWomanId(Long womanId);


    // =========================================================
    // FEATURED PRODUCTS
    // =========================================================

    List<Product> findByFeaturedTrueAndStatus(
            ProductStatus status
    );


    // =========================================================
    // VILLAGE PARTNER PRODUCTS
    // =========================================================
    // Gets products belonging to women managed by this partner.
    //
    // Product
    //    ↓
    // Woman
    //    ↓
    // VillagePartner
    //
    // Therefore a partner can only receive their own products.
    // =========================================================

    @Query("""
        SELECT p
        FROM Product p
        JOIN p.woman w
        WHERE w.partner.id = :partnerId
        ORDER BY p.createdAt DESC
    """)
    List<Product> findProductsForPartner(
            @Param("partnerId") Long partnerId
    );
}