package com.example.order_service.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Integer id,
        String orderCode,
        String supplierName,
        BigDecimal totalAmount,
        String status,
        Integer createdByUserId,
        Integer approvedByUserId,
        String createdAt,
        String approvedAt,
        String receivedAt,
        String cancelledAt,
        List<OrderItemResponse> items
) {}
