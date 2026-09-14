package com.madebyher.controller;

import com.madebyher.dto.ProductDtos.*;
import com.madebyher.model.Product;
import com.madebyher.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;


    // =========================================================
    // PUBLIC MARKETPLACE PRODUCTS
    // =========================================================

    @GetMapping
    public Page<Product> browse(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        return service.browse(
                search,
                categoryId,
                page,
                size
        );
    }


    // =========================================================
    // FEATURED PRODUCTS
    // =========================================================

    @GetMapping("/featured")
    public List<Product> featured() {

        return service.featured();
    }


    // =========================================================
    // LOGGED-IN VILLAGE PARTNER PRODUCTS
    // =========================================================

    @GetMapping("/partner")
    public List<Product> partnerProducts() {

        return service.partnerProducts();
    }


    // =========================================================
    // SINGLE PRODUCT
    // =========================================================

    @GetMapping("/{id}")
    public Product get(
            @PathVariable Long id) {

        return service.get(id);
    }


    // =========================================================
    // CREATE PRODUCT
    // =========================================================

    @PostMapping
    public Product create(
            @Valid @RequestBody ProductRequest req) {

        return service.create(req);
    }


    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @PutMapping("/{id}")
    public Product update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest req) {

        return service.update(id, req);
    }


    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id) {

        service.delete(id);
    }
}