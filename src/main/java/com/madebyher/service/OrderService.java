package com.madebyher.service;

import com.madebyher.dto.OrderDtos.*;
import com.madebyher.model.*;
import com.madebyher.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final PartnerRepository partnerRepository;
    private final CurrentUserService currentUser;


    // =========================================================
    // CUSTOMER - CREATE ORDER
    // =========================================================

    @Transactional
    public Order create(CreateOrderRequest req) {

        User customer = currentUser.get();

        Address address = addressRepository
                .findById(req.addressId())
                .orElseThrow(() ->
                        new RuntimeException("Address not found")
                );

        if (!address.getUser().getId().equals(customer.getId())) {
            throw new IllegalArgumentException(
                    "Address does not belong to you"
            );
        }

        Order order = Order.builder()
                .orderNumber(
                        "MBH-" +
                                System.currentTimeMillis() +
                                "-" +
                                ThreadLocalRandom.current()
                                        .nextInt(100, 999)
                )
                .customer(customer)
                .shippingAddress(address)
                .paymentMethod(req.paymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .status(OrderStatus.PLACED)
                .subtotal(BigDecimal.ZERO)
                .shippingFee(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItemRequest r : req.items()) {

            Product p = productRepository
                    .findById(r.productId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Product not found"
                            )
                    );

            if (p.getStatus() != ProductStatus.PUBLISHED) {
                throw new IllegalArgumentException(
                        "Product unavailable: " + p.getName()
                );
            }

            if (p.getStockQuantity() < r.quantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock: " + p.getName()
                );
            }

            BigDecimal line =
                    p.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(r.quantity())
                            );


            // =====================================================
            // CURRENT BUSINESS SPLIT
            // =====================================================
            // 70% -> Woman
            // 10% -> Village Partner
            // 20% -> Platform
            //
            // These are currently the percentages implemented
            // in your backend.
            // =====================================================

            BigDecimal woman =
                    line.multiply(new BigDecimal("0.70"));

            BigDecimal partner =
                    line.multiply(new BigDecimal("0.10"));

            BigDecimal platform =
                    line.multiply(new BigDecimal("0.20"));


            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(p)
                    .woman(p.getWoman())
                    .quantity(r.quantity())
                    .unitPrice(p.getPrice())
                    .lineTotal(line)
                    .womanEarning(woman)
                    .partnerCommission(partner)
                    .platformCommission(platform)
                    .build();

            order.getItems().add(item);

            // Reduce stock
            p.setStockQuantity(
                    p.getStockQuantity() - r.quantity()
            );

            // Automatically mark product out of stock
            if (p.getStockQuantity() == 0) {
                p.setStatus(ProductStatus.OUT_OF_STOCK);
            }

            productRepository.save(p);

            subtotal = subtotal.add(line);
        }

        order.setSubtotal(subtotal);
        order.setShippingFee(BigDecimal.ZERO);
        order.setTotalAmount(subtotal);

        Order saved = orderRepository.save(order);

        // Clear customer's cart
        cartRepository.deleteByUserId(customer.getId());

        return saved;
    }


    // =========================================================
    // VILLAGE PARTNER - REAL ORDERS
    // =========================================================

    public List<Order> partnerOrders() {

        User user = currentUser.get();

        if (user.getRole() != Role.VILLAGE_PARTNER) {
            throw new AccessDeniedException(
                    "Village Partner access required"
            );
        }

        VillagePartner partner = partnerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Partner profile not found"
                        )
                );

        return orderRepository.findOrdersForPartner(
                partner.getId()
        );
    }


    // =========================================================
    // CUSTOMER - MY ORDERS
    // =========================================================

    public List<Order> myOrders() {

        User user = currentUser.get();

        return orderRepository
                .findByCustomerIdOrderByCreatedAtDesc(
                        user.getId()
                );
    }


    // =========================================================
    // CUSTOMER - SINGLE ORDER
    // =========================================================

    public Order getMyOrder(Long id) {

        User user = currentUser.get();

        Order order = orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        )
                );

        if (!order.getCustomer()
                .getId()
                .equals(user.getId())) {

            throw new AccessDeniedException(
                    "Forbidden"
            );
        }

        return order;
    }


    // =========================================================
    // ADMIN - ALL ORDERS
    // =========================================================

    public List<Order> allOrders() {

        User user = currentUser.get();

        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException(
                    "Admin only"
            );
        }

        return orderRepository
                .findAllByOrderByCreatedAtDesc();
    }


    // =========================================================
    // UPDATE ORDER STATUS
    // =========================================================

    public Order updateStatus(
            Long id,
            OrderStatus status
    ) {

        User user = currentUser.get();

        if (user.getRole() != Role.ADMIN &&
                user.getRole() != Role.VILLAGE_PARTNER) {

            throw new AccessDeniedException(
                    "Forbidden"
            );
        }

        Order order = orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        )
                );


        // ---------------------------------------------------------
        // Village Partner can only update orders containing
        // products belonging to women managed by that partner.
        // ---------------------------------------------------------

        if (user.getRole() == Role.VILLAGE_PARTNER) {

            boolean ownsAtLeastOne =
                    order.getItems()
                            .stream()
                            .anyMatch(item ->

                                    item.getWoman() != null
                                            &&
                                            item.getWoman()
                                                    .getPartner() != null
                                            &&
                                            item.getWoman()
                                                    .getPartner()
                                                    .getUser() != null
                                            &&
                                            item.getWoman()
                                                    .getPartner()
                                                    .getUser()
                                                    .getId()
                                                    .equals(user.getId())
                            );

            if (!ownsAtLeastOne) {
                throw new AccessDeniedException(
                        "Not your order"
                );
            }
        }

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }


    // =========================================================
    // ADMIN - PAYMENT STATUS
    // =========================================================

    public Order markPayment(
            Long id,
            PaymentStatus status
    ) {

        if (currentUser.get().getRole() != Role.ADMIN) {
            throw new AccessDeniedException(
                    "Admin only"
            );
        }

        Order order = orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        )
                );

        order.setPaymentStatus(status);

        return orderRepository.save(order);
    }
}