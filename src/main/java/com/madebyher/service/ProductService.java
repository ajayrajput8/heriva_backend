package com.madebyher.service;

import com.madebyher.dto.ProductDtos.*;
import com.madebyher.model.*;
import com.madebyher.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final WomanRepository womanRepository;
    private final PartnerRepository partnerRepository;
    private final CurrentUserService currentUser;

    @Value("${app.partner.max-women:5}")
    private int maxWomen;


    // =========================================================
    // PUBLIC MARKETPLACE
    // =========================================================

    public Page<Product> browse(
            String search,
            Long categoryId,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page,
                Math.min(size, 50),
                Sort.by("createdAt").descending()
        );

        if (search != null && !search.isBlank()) {
            return productRepository
                    .findByStatusAndNameContainingIgnoreCase(
                            ProductStatus.PUBLISHED,
                            search,
                            pageable
                    );
        }

        if (categoryId != null) {
            return productRepository
                    .findByStatusAndCategoryId(
                            ProductStatus.PUBLISHED,
                            categoryId,
                            pageable
                    );
        }

        return productRepository.findByStatus(
                ProductStatus.PUBLISHED,
                pageable
        );
    }


    // =========================================================
    // SINGLE PRODUCT
    // =========================================================

    public Product get(Long id) {

        return productRepository
                .findById(id)
                .orElseThrow();
    }


    // =========================================================
    // FEATURED PRODUCTS
    // =========================================================

    public List<Product> featured() {

        return productRepository
                .findByFeaturedTrueAndStatus(
                        ProductStatus.PUBLISHED
                );
    }


    // =========================================================
    // PRODUCTS OF LOGGED-IN VILLAGE PARTNER
    // =========================================================

    public List<Product> partnerProducts() {

        User user = currentUser.get();

        // Only Village Partners can access this endpoint
        UserGuard.requireRole(
                user,
                Role.VILLAGE_PARTNER
        );

        // Find the Village Partner profile belonging
        // to the currently logged-in user
        VillagePartner partner = partnerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Partner profile not found"
                        )
                );

        // Return ONLY products belonging to this partner's women
        return productRepository.findProductsForPartner(
                partner.getId()
        );
    }


    // =========================================================
    // CREATE PRODUCT
    // =========================================================

    public Product create(ProductRequest req) {

        User user = currentUser.get();

        UserGuard.requireRole(
                user,
                Role.VILLAGE_PARTNER
        );

        Woman woman = womanRepository
                .findById(req.womanId())
                .orElseThrow();

        VillagePartner partner = partnerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Partner profile not found"
                        )
                );

        // SECURITY:
        // The selected woman MUST belong to this partner.
        if (woman.getPartner() == null ||
                !woman.getPartner()
                        .getId()
                        .equals(partner.getId())) {

            throw new IllegalArgumentException(
                    "You can only manage your assigned women"
            );
        }

        Category category = categoryRepository
                .findById(req.categoryId())
                .orElseThrow();

        Product p = Product.builder()
                .name(req.name())
                .description(req.description())
                .price(req.price())
                .stockQuantity(req.stockQuantity())
                .category(category)
                .woman(woman)
                .mainImageUrl(req.mainImageUrl())
                .imageUrls(
                        req.imageUrls() == null
                                ? new java.util.ArrayList<>()
                                : req.imageUrls()
                )
                .weightKg(req.weightKg())
                .featured(
                        Boolean.TRUE.equals(
                                req.featured()
                        )
                )
                .status(
                        ProductStatus.PENDING_APPROVAL
                )
                .build();

        return productRepository.save(p);
    }


    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    public Product update(
            Long id,
            ProductRequest req) {

        User user = currentUser.get();

        UserGuard.requireRole(
                user,
                Role.VILLAGE_PARTNER
        );

        VillagePartner partner = partnerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Partner profile not found"
                        )
                );

        Product p = get(id);

        // SECURITY:
        // Product must belong to one of this partner's women.
        if (p.getWoman() == null ||
                p.getWoman().getPartner() == null ||
                !p.getWoman()
                        .getPartner()
                        .getId()
                        .equals(partner.getId())) {

            throw new IllegalArgumentException(
                    "Not your product"
            );
        }

        // SECURITY:
        // If the partner changes the Woman Artisan
        // while editing, make sure the new woman
        // also belongs to this partner.
        Woman woman = womanRepository
                .findById(req.womanId())
                .orElseThrow();

        if (woman.getPartner() == null ||
                !woman.getPartner()
                        .getId()
                        .equals(partner.getId())) {

            throw new IllegalArgumentException(
                    "You can only assign products to your assigned women"
            );
        }

        Category category = categoryRepository
                .findById(req.categoryId())
                .orElseThrow();

        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setStockQuantity(req.stockQuantity());
        p.setCategory(category);
        p.setWoman(woman);
        p.setMainImageUrl(req.mainImageUrl());

        p.setImageUrls(
                req.imageUrls() == null
                        ? new java.util.ArrayList<>()
                        : req.imageUrls()
        );

        p.setWeightKg(req.weightKg());

        p.setFeatured(
                Boolean.TRUE.equals(
                        req.featured()
                )
        );

        p.setUpdatedAt(
                LocalDateTime.now()
        );

        return productRepository.save(p);
    }


    // =========================================================
    // DELETE / ARCHIVE PRODUCT
    // =========================================================

    public void delete(Long id) {

        User user = currentUser.get();

        UserGuard.requireRole(
                user,
                Role.VILLAGE_PARTNER
        );

        VillagePartner partner = partnerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Partner profile not found"
                        )
                );

        Product p = get(id);

        // SECURITY:
        // Partner can only archive their own product.
        if (p.getWoman() == null ||
                p.getWoman().getPartner() == null ||
                !p.getWoman()
                        .getPartner()
                        .getId()
                        .equals(partner.getId())) {

            throw new IllegalArgumentException(
                    "Not your product"
            );
        }

        // Soft delete
        p.setStatus(
                ProductStatus.ARCHIVED
        );

        p.setUpdatedAt(
                LocalDateTime.now()
        );

        productRepository.save(p);
    }


    // =========================================================
    // ADMIN APPROVAL
    // =========================================================

    public void approve(
            Long id,
            boolean approve) {

        UserGuard.requireRole(
                currentUser.get(),
                Role.ADMIN
        );

        Product p = get(id);

        p.setStatus(
                approve
                        ? ProductStatus.PUBLISHED
                        : ProductStatus.REJECTED
        );

        p.setUpdatedAt(
                LocalDateTime.now()
        );

        productRepository.save(p);
    }


    // =========================================================
    // ROLE GUARD
    // =========================================================

    static class UserGuard {

        static void requireRole(
                User user,
                Role role) {

            if (user == null ||
                    user.getRole() != role) {

                throw new org.springframework
                        .security
                        .access
                        .AccessDeniedException(
                        "Forbidden"
                );
            }
        }
    }
}