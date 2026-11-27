package com.example.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderRequest(
        @Size(max = 200, message = "Tên nhà cung cấp tối đa 200 ký tự")
        String supplierName,

        @Positive(message = "Mã nhân viên tạo đơn phải lớn hơn 0")
        Integer createdByUserId,

        @NotEmpty(message = "Đơn hàng phải có sản phẩm")
        List<@NotNull(message = "Chi tiết sản phẩm không được để trống") @Valid OrderItemRequest> items
) {}
