package com.madebyher.repository;
import com.madebyher.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdAndApprovedTrueOrderByCreatedAtDesc(Long productId);
    Optional<Review> findByCustomerIdAndProductId(Long customerId, Long productId);
}
