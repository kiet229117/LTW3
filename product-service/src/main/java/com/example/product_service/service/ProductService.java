package com.example.product_service.service;

import com.example.product_service.entity.Brand;
import com.example.product_service.entity.Category;
import com.example.product_service.entity.Product;
import com.example.product_service.entity.ProductImage;
import com.example.product_service.repository.BrandRepository;
import com.example.product_service.repository.CategoryRepository;
import com.example.product_service.repository.ProductImageRepository;
import com.example.product_service.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductImageRepository productImageRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          BrandRepository brandRepository,
                          ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productImageRepository = productImageRepository;
    }

    @PostConstruct
    public void initSeedData() {
        try {
            Category accCat = categoryRepository.findByNameIgnoreCase("Accessories")
                    .orElseGet(() -> categoryRepository.save(new Category("Accessories", "Phụ kiện máy tính (Chuột, Bàn phím)")));
            Category dispCat = categoryRepository.findByNameIgnoreCase("Display")
                    .orElseGet(() -> categoryRepository.save(new Category("Display", "Màn hình máy tính")));

            Brand logiBrand = brandRepository.findByNameIgnoreCase("Logitech")
                    .orElseGet(() -> brandRepository.save(new Brand("Logitech", "Thương hiệu thiết bị ngoại vi Logitech")));
            Brand keychronBrand = brandRepository.findByNameIgnoreCase("Keychron")
                    .orElseGet(() -> brandRepository.save(new Brand("Keychron", "Thương hiệu bàn phím cơ Keychron")));
            Brand dellBrand = brandRepository.findByNameIgnoreCase("Dell")
                    .orElseGet(() -> brandRepository.save(new Brand("Dell", "Thương hiệu màn hình và máy tính Dell")));

            if (productRepository.count() == 0) {
                Product p1 = productRepository.save(new Product("SKU-MOU-001", "Mouse Logitech G102", "Accessories", "Cái", 150000, "ACTIVE", accCat, logiBrand));
                Product p2 = productRepository.save(new Product("SKU-KEY-002", "Keyboard Mechanical Keychron", "Accessories", "Cái", 250000, "ACTIVE", accCat, keychronBrand));
                Product p3 = productRepository.save(new Product("SKU-MON-003", "Monitor Dell UltraSharp 24", "Display", "Chiếc", 2500000, "ACTIVE", dispCat, dellBrand));

                productImageRepository.save(new ProductImage("https://images.example.com/products/logitech-g102.jpg", p1));
                productImageRepository.save(new ProductImage("https://images.example.com/products/keychron-k2.jpg", p2));
                productImageRepository.save(new ProductImage("https://images.example.com/products/dell-ultrasharp-24.jpg", p3));
            }
        } catch (Exception ignored) {
        }
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Optional<Product> getById(int id) {
        return productRepository.findById(id);
    }

    public Product create(Map<String, Object> payload) {
        Product product = new Product();
        String categoryName = (String) payload.getOrDefault("category", "General");
        product.setCategory(categoryName);
        product.setUnit((String) payload.getOrDefault("unit", "Cái"));
        product.setPrice(((Number) payload.getOrDefault("price", 0)).intValue());
        product.setStatus((String) payload.getOrDefault("status", "ACTIVE"));
        product.setSku((String) payload.get("sku"));
        product.setName(payload.get("name") != null ? String.valueOf(payload.get("name")) : "Sản phẩm mới");

        if (payload.get("categoryId") instanceof Number catId) {
            categoryRepository.findById(catId.intValue()).ifPresent(cat -> {
                product.setCategoryEntity(cat);
                product.setCategory(cat.getName());
            });
        } else {
            categoryRepository.findByNameIgnoreCase(categoryName).ifPresent(product::setCategoryEntity);
        }

        if (payload.get("brandId") instanceof Number bId) {
            brandRepository.findById(bId.intValue()).ifPresent(product::setBrandEntity);
        }

        Product saved = productRepository.save(product);
        boolean needsUpdate = false;
        if (saved.getSku() == null || saved.getSku().isBlank()) {
            saved.setSku(String.format("SKU-PRD-%03d", saved.getId()));
            needsUpdate = true;
        }
        if (!payload.containsKey("name")) {
            saved.setName("Sản phẩm mới " + saved.getId());
            needsUpdate = true;
        }
        if (needsUpdate) {
            saved = productRepository.save(saved);
        }
        return saved;
    }

    public Optional<Product> update(int id, Map<String, Object> payload) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            return Optional.empty();
        }

        Product product = productOpt.get();
        if (payload.containsKey("sku")) product.setSku(String.valueOf(payload.get("sku")));
        if (payload.containsKey("name")) product.setName(String.valueOf(payload.get("name")));
        if (payload.containsKey("category")) product.setCategory(String.valueOf(payload.get("category")));
        if (payload.containsKey("unit")) product.setUnit(String.valueOf(payload.get("unit")));
        if (payload.containsKey("price")) product.setPrice(((Number) payload.get("price")).intValue());
        if (payload.containsKey("status")) product.setStatus(String.valueOf(payload.get("status")));
        if (payload.get("categoryId") instanceof Number catId) {
            categoryRepository.findById(catId.intValue()).ifPresent(product::setCategoryEntity);
        }
        if (payload.get("brandId") instanceof Number bId) {
            brandRepository.findById(bId.intValue()).ifPresent(product::setBrandEntity);
        }

        return Optional.of(productRepository.save(product));
    }

    public boolean delete(int id) {
        if (!productRepository.existsById(id)) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }

    public Optional<Product> getBySku(String sku) {
        return productRepository.findBySkuIgnoreCase(sku);
    }

    public List<Product> search(String keyword, String category) {
        return productRepository.findAll().stream()
                .filter(p -> {
                    boolean matchKeyword = keyword == null || keyword.isBlank()
                            || (p.getName() != null && p.getName().toLowerCase().contains(keyword.toLowerCase()))
                            || (p.getSku() != null && p.getSku().toLowerCase().contains(keyword.toLowerCase()));
                    boolean matchCategory = category == null || category.isBlank()
                            || (p.getCategory() != null && category.equalsIgnoreCase(p.getCategory()));
                    return matchKeyword && matchCategory;
                })
                .toList();
    }
}
