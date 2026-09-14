package com.madebyher.controller;

import com.madebyher.model.*;
import com.madebyher.repository.*;
import com.madebyher.service.PartnerService;
import com.madebyher.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final PartnerRepository partnerRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final PartnerService partnerService;
    private final CategoryRepository categoryRepository;
    private final WomanRepository womanRepository;
    private final ProductRepository productRepository;


    // =========================================================
    // DASHBOARD
    // =========================================================

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {

        return Map.of(
                "totalUsers", userRepository.count(),
                "totalPartners", partnerRepository.count(),
                "totalWomen", womanRepository.count(),
                "totalCategories", categoryRepository.count(),
                "pendingPartners",
                partnerRepository.findByStatus(PartnerStatus.PENDING).size()
        );
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // =========================================================
    // PRODUCTS
    // =========================================================

    // Get ALL products
    @GetMapping("/products")
    public Page<Product> getAllProducts(Pageable pageable) {

        return productRepository.findAll(pageable);
    }


    // Get PENDING products
    @GetMapping("/products/pending")
    public Page<Product> getPendingProducts(Pageable pageable) {

        return productRepository.findByStatus(
                ProductStatus.PENDING_APPROVAL,
                pageable
        );
    }


    // Get PUBLISHED products
    @GetMapping("/products/published")
    public Page<Product> getPublishedProducts(Pageable pageable) {

        return productRepository.findByStatus(
                ProductStatus.PUBLISHED,
                pageable
        );
    }


    // Approve / Reject product
    @PatchMapping("/products/{id}/approve")
    public Map<String, String> approveProduct(
            @PathVariable Long id,
            @RequestParam boolean approve
    ) {

        productService.approve(id, approve);

        return Map.of(
                "message",
                approve
                        ? "Product published"
                        : "Product rejected"
        );
    }


    // Hide / Show product in shop
    @PatchMapping("/products/{id}/visibility")
    public Map<String, String> updateProductVisibility(
            @PathVariable Long id,
            @RequestParam boolean visible
    ) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found with id: " + id
                        )
                );

        if (visible) {

            product.setStatus(ProductStatus.PUBLISHED);

        } else {

            product.setStatus(ProductStatus.HIDDEN);
        }

        productRepository.save(product);

        return Map.of(
                "message",
                visible
                        ? "Product is now visible in shop"
                        : "Product hidden from shop"
        );
    }


    // =========================================================
    // WOMEN
    // =========================================================

    // Get all women
    @GetMapping("/women")
    public List<Woman> getAllWomen() {

        return womanRepository.findAll();
    }


    // Get woman by ID
    @GetMapping("/women/{id}")
    public Woman getWoman(@PathVariable Long id) {

        return womanRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Woman not found with id: " + id
                        )
                );
    }


    // Get women managed by a particular partner
    @GetMapping("/partners/{partnerId}/women")
    public List<Woman> getWomenByPartner(
            @PathVariable Long partnerId
    ) {

        return womanRepository.findByPartnerId(partnerId);
    }


    // =========================================================
    // VILLAGE PARTNERS
    // =========================================================

    // Pending partner applications
    @GetMapping("/partners/pending")
    public List<VillagePartner> pendingPartners() {

        return partnerRepository.findByStatus(
                PartnerStatus.PENDING
        );
    }


    // Approved / active partners
    @GetMapping("/partners/active")
    public List<VillagePartner> getActivePartners() {

        return partnerRepository.findByStatus(
                PartnerStatus.APPROVED
        );
    }


    // Approve / reject partner
    @PatchMapping("/partners/{id}/approve")
    public Map<String, String> approvePartner(
            @PathVariable Long id,
            @RequestParam boolean approve
    ) {

        partnerService.approve(id, approve);

        return Map.of(
                "message",
                approve
                        ? "Partner approved"
                        : "Partner rejected"
        );
    }


    // =========================================================
    // CATEGORIES
    // =========================================================

    // Create category
    @PostMapping("/categories")
    public Category createCategory(
            @RequestBody Category category
    ) {

        category.setId(null);

        return categoryRepository.save(category);
    }


    // Update category
    @PutMapping("/categories/{id}")
    public Category updateCategory(
            @PathVariable Long id,
            @RequestBody Category incoming
    ) {

        Category c = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found with id: " + id
                        )
                );

        c.setName(incoming.getName());
        c.setDescription(incoming.getDescription());
        c.setImageUrl(incoming.getImageUrl());
        c.setActive(incoming.isActive());

        return categoryRepository.save(c);
    }


    // Delete category
    @DeleteMapping("/categories/{id}")
    public void deleteCategory(
            @PathVariable Long id
    ) {

        Category c = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found with id: " + id
                        )
                );

        // Soft delete
        c.setActive(false);

        categoryRepository.save(c);
    }
}