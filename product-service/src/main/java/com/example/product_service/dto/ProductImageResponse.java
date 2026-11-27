package com.example.product_service.dto;

public record ProductImageResponse(
        Integer id,
        String imageUrl,
        Integer productId
) {}
