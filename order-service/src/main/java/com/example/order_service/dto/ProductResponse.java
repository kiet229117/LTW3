package com.example.order_service.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Integer id,
        String sku,
        String name,
        String unit,
        BigDecimal price,
        String status
) {}
