package com.example.product_service.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Integer id,
        String sku,
        String name,
        String category,
        String unit,
        BigDecimal price,
        String status,
        Integer categoryId,
        String categoryName,
        Integer brandId,
        String brandName,
        List<String> imageUrls
) {}
