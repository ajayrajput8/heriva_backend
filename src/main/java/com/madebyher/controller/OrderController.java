package com.madebyher.controller;

import com.madebyher.dto.OrderDtos.CreateOrderRequest;
import com.madebyher.model.*;
import com.madebyher.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    // =========================
    // CUSTOMER
    // =========================

    @PostMapping
    public Order create(
            @Valid @RequestBody CreateOrderRequest req
    ) {
        return service.create(req);
    }

    @GetMapping("/my")
    public List<Order> myOrders() {
        return service.myOrders();
    }

    @GetMapping("/my/{id}")
    public Order myOrder(
            @PathVariable Long id
    ) {
        return service.getMyOrder(id);
    }


    // =========================
    // VILLAGE PARTNER
    // =========================

    @GetMapping("/partner")
    public List<Order> partnerOrders() {
        return service.partnerOrders();
    }


    // =========================
    // ADMIN
    // =========================

    @GetMapping("/admin")
    public List<Order> allOrders() {
        return service.allOrders();
    }


    // =========================
    // ORDER STATUS
    // =========================

    @PatchMapping("/{id}/status")
    public Order status(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {
        return service.updateStatus(id, status);
    }


    // =========================
    // PAYMENT STATUS
    // =========================

    @PatchMapping("/{id}/payment")
    public Order payment(
            @PathVariable Long id,
            @RequestParam PaymentStatus status
    ) {
        return service.markPayment(id, status);
    }
}