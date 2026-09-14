package com.madebyher.controller;

import com.madebyher.model.Review;
import com.madebyher.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;

    @GetMapping
    public List<Review> list(@PathVariable Long productId) {
        return service.list(productId);
    }

    @PostMapping
    public Review create(
            @PathVariable Long productId,
            @RequestParam int rating,
            @RequestParam(required=false) String comment) {
        return service.create(productId, rating, comment);
    }
}
