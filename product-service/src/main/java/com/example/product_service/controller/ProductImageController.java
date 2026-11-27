package com.example.product_service.controller;

import com.example.product_service.dto.ProductImageRequest;
import com.example.product_service.dto.ProductImageResponse;
import com.example.product_service.entity.Product;
import com.example.product_service.entity.ProductImage;
import com.example.product_service.exception.ResourceNotFoundException;
import com.example.product_service.repository.ProductImageRepository;
import com.example.product_service.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product-images")
public class ProductImageController {

    private final ProductImageRepository imageRepository;
    private final ProductRepository productRepository;

    public ProductImageController(ProductImageRepository imageRepository, ProductRepository productRepository) {
        this.imageRepository = imageRepository;
        this.productRepository = productRepository;
    }

    private ProductImageResponse toResponse(ProductImage img) {
        return new ProductImageResponse(
                img.getId(),
                img.getImageUrl(),
                img.getProduct() != null ? img.getProduct().getId() : null
        );
    }

    @GetMapping
    public List<ProductImageResponse> getAll() {
        return imageRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductImageResponse getById(@PathVariable Integer id) {
        ProductImage img = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hình ảnh: " + id));
        return toResponse(img);
    }

    @PostMapping
    public ResponseEntity<ProductImageResponse> create(@Valid @RequestBody ProductImageRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm: " + request.productId()));

        ProductImage saved = imageRepository.save(new ProductImage(request.imageUrl().trim(), product));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        if (!imageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy hình ảnh: " + id);
        }
        imageRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa hình ảnh sản phẩm ID = " + id));
    }
}
