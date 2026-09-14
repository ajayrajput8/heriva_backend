package com.madebyher.service;

import com.madebyher.model.*;
import com.madebyher.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository repository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUser;

    public List<WishlistItem> get() {
        return repository.findByUserId(currentUser.get().getId());
    }

    public void toggle(Long productId) {
        User user = currentUser.get();
        var existing = repository.findByUserIdAndProductId(user.getId(), productId);
        if (existing.isPresent()) repository.delete(existing.get());
        else repository.save(WishlistItem.builder()
                .user(user)
                .product(productRepository.findById(productId).orElseThrow())
                .build());
    }
}
