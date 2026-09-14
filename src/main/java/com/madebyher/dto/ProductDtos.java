package com.madebyher.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductDtos {
    public record ProductRequest(
            @NotBlank String name,
            String description,
            @NotNull @Positive BigDecimal price,
            @NotNull @PositiveOrZero Integer stockQuantity,
            @NotNull Long categoryId,
            @NotNull Long womanId,
            String mainImageUrl,
            List<String> imageUrls,
            Double weightKg,
            Boolean featured) {}

    public record StockRequest(@NotNull @PositiveOrZero Integer quantity) {}
}
