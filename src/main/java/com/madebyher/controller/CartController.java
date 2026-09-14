package com.madebyher.controller;

import com.madebyher.model.CartItem;
import com.madebyher.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService service;

    @GetMapping
    public List<CartItem> get() { return service.get(); }

    @PostMapping("/items")
    public CartItem add(@RequestParam Long productId, @RequestParam(defaultValue="1") int quantity) {
        return service.add(productId, quantity);
    }

    @PutMapping("/items/{itemId}")
    public CartItem update(@PathVariable Long itemId, @RequestParam int quantity) {
        return service.update(itemId, quantity);
    }

    @DeleteMapping("/items/{itemId}")
    public Map<String,String> remove(@PathVariable Long itemId) {
        service.remove(itemId);
        return Map.of("message","Removed from cart");
    }
}
