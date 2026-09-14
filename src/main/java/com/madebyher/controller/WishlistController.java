package com.madebyher.controller;

import com.madebyher.model.WishlistItem;
import com.madebyher.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService service;

    @GetMapping
    public List<WishlistItem> get() { return service.get(); }

    @PostMapping("/toggle/{productId}")
    public Map<String,String> toggle(@PathVariable Long productId) {
        service.toggle(productId);
        return Map.of("message","Wishlist updated");
    }
}
