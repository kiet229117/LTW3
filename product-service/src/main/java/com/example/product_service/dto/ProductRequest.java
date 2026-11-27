package com.example.product_service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(
        @Size(max = 50, message = "Mã SKU tối đa 50 ký tự")
        String sku,

        @NotBlank(message = "Tên sản phẩm không được để trống")
        @Size(max = 200, message = "Tên sản phẩm tối đa 200 ký tự")
        String name,

        @Size(max = 100, message = "Tên danh mục tối đa 100 ký tự")
        String category,

        @Size(max = 50, message = "Đơn vị tính tối đa 50 ký tự")
        String unit,

        @NotNull(message = "Giá không được để trống")
        @Positive(message = "Giá phải lớn hơn 0")
        BigDecimal price,

        @Size(max = 30, message = "Trạng thái tối đa 30 ký tự")
        String status,

        @Positive(message = "Mã danh mục phải lớn hơn 0")
        Integer categoryId,

        @Positive(message = "Mã thương hiệu phải lớn hơn 0")
        Integer brandId,

        @Size(max = 500, message = "Đường dẫn hình ảnh tối đa 500 ký tự")
        String imageUrl
) {}
