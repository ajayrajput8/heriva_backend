package com.madebyher.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class OrderDtos {
    public record OrderItemRequest(@NotNull Long productId, @NotNull @Positive Integer quantity) {}

    public record CreateOrderRequest(
            @NotNull Long addressId,
            @NotEmpty List<OrderItemRequest> items,
            @NotBlank String paymentMethod) {}
}
