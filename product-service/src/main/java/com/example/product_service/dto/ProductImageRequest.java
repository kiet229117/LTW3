package com.example.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductImageRequest(
        @NotBlank(message = "Đường dẫn hình ảnh không được để trống")
        @Size(max = 500, message = "Đường dẫn hình ảnh tối đa 500 ký tự")
        String imageUrl,

        @NotNull(message = "Mã sản phẩm không được để trống")
        @Positive(message = "Mã sản phẩm phải lớn hơn 0")
        Integer productId
) {}
