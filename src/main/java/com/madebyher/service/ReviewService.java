package com.madebyher.service;

import com.madebyher.model.*;
import com.madebyher.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUser;

    public List<Review> list(Long productId) {
        return reviewRepository.findByProductIdAndApprovedTrueOrderByCreatedAtDesc(productId);
    }

    public Review create(Long productId, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1-5");
        User u = currentUser.get();
        if (reviewRepository.findByCustomerIdAndProductId(u.getId(), productId).isPresent())
            throw new IllegalArgumentException("You already reviewed this product");
        return reviewRepository.save(Review.builder()
                .customer(u)
                .product(productRepository.findById(productId).orElseThrow())
                .rating(rating)
                .comment(comment)
                .approved(true)
                .build());
    }
}
