package com.madebyher.service;

import com.madebyher.model.*;
import com.madebyher.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUser;

    public List<CartItem> get() {
        return cartRepository.findByUserId(currentUser.get().getId());
    }

    public CartItem add(Long productId, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be at least 1");
        User user = currentUser.get();
        Product p = productRepository.findById(productId).orElseThrow();
        if (p.getStatus() != ProductStatus.PUBLISHED)
            throw new IllegalArgumentException("Product is not available");

        CartItem item = cartRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElse(CartItem.builder().user(user).product(p).quantity(0).build());
        int newQty = item.getQuantity() + quantity;
        if (newQty > p.getStockQuantity())
            throw new IllegalArgumentException("Not enough stock");
        item.setQuantity(newQty);
        return cartRepository.save(item);
    }

    public CartItem update(Long itemId, int quantity) {
        CartItem item = cartRepository.findById(itemId).orElseThrow();
        if (!item.getUser().getId().equals(currentUser.get().getId()))
            throw new org.springframework.security.access.AccessDeniedException("Forbidden");
        if (quantity < 1 || quantity > item.getProduct().getStockQuantity())
            throw new IllegalArgumentException("Invalid quantity");
        item.setQuantity(quantity);
        return cartRepository.save(item);
    }

    public void remove(Long itemId) {
        CartItem item = cartRepository.findById(itemId).orElseThrow();
        if (!item.getUser().getId().equals(currentUser.get().getId()))
            throw new org.springframework.security.access.AccessDeniedException("Forbidden");
        cartRepository.delete(item);
    }
}
