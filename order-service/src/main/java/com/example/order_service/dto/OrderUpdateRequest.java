package com.example.order_service.dto;

import jakarta.validation.constraints.Size;

public record OrderUpdateRequest(
        @Size(max = 200, message = "Tên nhà cung cấp tối đa 200 ký tự")
        String supplierName,

        @Size(max = 30, message = "Trạng thái tối đa 30 ký tự")
        String status
) {}
