package com.example.product_service.controller;

import com.example.product_service.entity.Product;
import com.example.product_service.entity.ProductImage;
import com.example.product_service.repository.ProductImageRepository;
import com.example.product_service.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/product-images")
public class ProductImageController {

    private final ProductImageRepository imageRepository;
    private final ProductRepository productRepository;

    public ProductImageController(ProductImageRepository imageRepository, ProductRepository productRepository) {
        this.imageRepository = imageRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<ProductImage> getAll() {
        return imageRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductImage> getById(@PathVariable int id) {
        return imageRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload) {
        String imageUrl = (String) payload.get("imageUrl");
        if (imageUrl == null || imageUrl.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "imageUrl không được để trống"));
        }
        int productId = ((Number) payload.getOrDefault("productId", 1)).intValue();
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy sản phẩm ID = " + productId));
        }

        ProductImage saved = imageRepository.save(new ProductImage(imageUrl, productOpt.get()));
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable int id) {
        if (!imageRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        imageRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa hình ảnh sản phẩm ID = " + id));
    }
}
