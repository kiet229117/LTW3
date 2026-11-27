package com.example.product_service.service;

import com.example.product_service.dto.ProductRequest;
import com.example.product_service.dto.ProductResponse;
import com.example.product_service.entity.Brand;
import com.example.product_service.entity.Category;
import com.example.product_service.entity.Product;
import com.example.product_service.entity.ProductImage;
import com.example.product_service.exception.ResourceNotFoundException;
import com.example.product_service.repository.BrandRepository;
import com.example.product_service.repository.CategoryRepository;
import com.example.product_service.repository.ProductImageRepository;
import com.example.product_service.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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
                    .orElseGet(() -> categoryRepository.save(new Category("Accessories", "Phụ kiện máy tính (Chuột, Bàn phím, Lót chuột)")));
            Category dispCat = categoryRepository.findByNameIgnoreCase("Display")
                    .orElseGet(() -> categoryRepository.save(new Category("Display", "Màn hình máy tính văn phòng & đồ họa")));
            Category laptopCat = categoryRepository.findByNameIgnoreCase("Laptop")
                    .orElseGet(() -> categoryRepository.save(new Category("Laptop", "Máy tính xách tay doanh nghiệp & lập trình")));
            Category audioCat = categoryRepository.findByNameIgnoreCase("Audio")
                    .orElseGet(() -> categoryRepository.save(new Category("Audio", "Tai nghe, loa và thiết bị âm thanh")));
            Category storageCat = categoryRepository.findByNameIgnoreCase("Storage")
                    .orElseGet(() -> categoryRepository.save(new Category("Storage", "Ổ cứng SSD, HDD và thiết bị lưu trữ")));

            Brand logiBrand = brandRepository.findByNameIgnoreCase("Logitech")
                    .orElseGet(() -> brandRepository.save(new Brand("Logitech", "Thương hiệu thiết bị ngoại vi Logitech")));
            Brand keychronBrand = brandRepository.findByNameIgnoreCase("Keychron")
                    .orElseGet(() -> brandRepository.save(new Brand("Keychron", "Thương hiệu bàn phím cơ Keychron")));
            Brand dellBrand = brandRepository.findByNameIgnoreCase("Dell")
                    .orElseGet(() -> brandRepository.save(new Brand("Dell", "Thương hiệu màn hình và máy tính Dell")));
            Brand sonyBrand = brandRepository.findByNameIgnoreCase("Sony")
                    .orElseGet(() -> brandRepository.save(new Brand("Sony", "Thương hiệu thiết bị âm thanh Sony")));
            Brand samsungBrand = brandRepository.findByNameIgnoreCase("Samsung")
                    .orElseGet(() -> brandRepository.save(new Brand("Samsung", "Thương hiệu ổ cứng và màn hình Samsung")));
            Brand lgBrand = brandRepository.findByNameIgnoreCase("LG")
                    .orElseGet(() -> brandRepository.save(new Brand("LG", "Thương hiệu màn hình LG UltraGear & UltraWide")));

            if (productRepository.count() == 0) {
                seedProduct("SKU-MOU-001", "Mouse Logitech G102 Lightsync", "Accessories", "Cái", 390000, "ACTIVE", accCat, logiBrand,
                        "https://images.unsplash.com/photo-1527814050087-3793815479db?w=400");
                seedProduct("SKU-KEY-002", "Keyboard Mechanical Keychron K2 Pro", "Accessories", "Cái", 2150000, "ACTIVE", accCat, keychronBrand,
                        "https://images.unsplash.com/photo-1595225476474-87563907a212?w=400");
                seedProduct("SKU-MON-003", "Monitor Dell UltraSharp 24 U2424H", "Display", "Chiếc", 6490000, "ACTIVE", dispCat, dellBrand,
                        "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400");
                seedProduct("SKU-LAP-004", "Laptop Dell Inspiron 15 3530", "Laptop", "Chiếc", 15990000, "ACTIVE", laptopCat, dellBrand,
                        "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400");
                seedProduct("SKU-AUD-005", "Tai nghe Sony WH-1000XM5", "Audio", "Cái", 7990000, "ACTIVE", audioCat, sonyBrand,
                        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400");
                seedProduct("SKU-MOU-006", "Mouse Logitech MX Master 3S", "Accessories", "Cái", 2390000, "ACTIVE", accCat, logiBrand,
                        "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=400");
                seedProduct("SKU-STO-007", "SSD Samsung 990 Pro 1TB NVMe", "Storage", "Cái", 2890000, "ACTIVE", storageCat, samsungBrand,
                        "https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=400");
                seedProduct("SKU-MON-008", "Monitor LG UltraGear 27GN800 2K", "Display", "Chiếc", 7290000, "ACTIVE", dispCat, lgBrand,
                        "https://images.unsplash.com/photo-1586210579191-33b45e38fa2c?w=400");
                seedProduct("SKU-KEY-009", "Keyboard Keychron Q1 QMK Custom", "Accessories", "Cái", 3850000, "ACTIVE", accCat, keychronBrand,
                        "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400");
                seedProduct("SKU-STO-010", "SSD Portable Samsung T7 Shield 1TB", "Storage", "Cái", 2490000, "ACTIVE", storageCat, samsungBrand,
                        "https://images.unsplash.com/photo-1531492746076-161ca9bcad58?w=400");
            }
        } catch (Exception ignored) {
        }
    }

    private void seedProduct(String sku, String name, String category, String unit, int price, String status,
                             Category categoryEntity, Brand brandEntity, String imageUrl) {
        Product saved = productRepository.save(new Product(sku, name, category, unit, price, status, categoryEntity, brandEntity));
        if (imageUrl != null && !imageUrl.isBlank()) {
            productImageRepository.save(new ProductImage(imageUrl, saved));
        }
    }

    private ProductResponse toResponse(Product product) {
        List<String> imageUrls = product.getImages() == null
                ? List.of()
                : product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getCategory(),
                product.getUnit(),
                product.getPrice() != null ? BigDecimal.valueOf(product.getPrice()) : BigDecimal.ZERO,
                product.getStatus(),
                product.getCategoryEntity() != null ? product.getCategoryEntity().getId() : null,
                product.getCategoryEntity() != null ? product.getCategoryEntity().getName() : product.getCategory(),
                product.getBrandEntity() != null ? product.getBrandEntity().getId() : null,
                product.getBrandEntity() != null ? product.getBrandEntity().getName() : null,
                imageUrls
        );
    }

    public List<ProductResponse> getAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm: " + id));
        return toResponse(product);
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        String categoryName = (request.category() != null && !request.category().isBlank())
                ? request.category()
                : "General";
        product.setSku(request.sku());
        product.setName(request.name().trim());
        product.setCategory(categoryName);
        product.setUnit((request.unit() != null && !request.unit().isBlank()) ? request.unit() : "Cái");
        product.setPrice(request.price().intValue());
        product.setStatus((request.status() != null && !request.status().isBlank()) ? request.status() : "ACTIVE");

        if (request.categoryId() != null) {
            Category cat = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục: " + request.categoryId()));
            product.setCategoryEntity(cat);
            product.setCategory(cat.getName());
        } else {
            categoryRepository.findByNameIgnoreCase(categoryName).ifPresent(product::setCategoryEntity);
        }

        if (request.brandId() != null) {
            Brand brand = brandRepository.findById(request.brandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu: " + request.brandId()));
            product.setBrandEntity(brand);
        }

        Product saved = productRepository.save(product);
        if (saved.getSku() == null || saved.getSku().isBlank()) {
            saved.setSku(String.format("SKU-PRD-%03d", saved.getId()));
            saved = productRepository.save(saved);
        }

        if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
            ProductImage img = productImageRepository.save(new ProductImage(request.imageUrl().trim(), saved));
            saved.getImages().add(img);
        }

        return toResponse(saved);
    }

    public ProductResponse update(Integer id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm: " + id));

        if (request.sku() != null && !request.sku().isBlank()) {
            product.setSku(request.sku().trim());
        }
        product.setName(request.name().trim());
        if (request.category() != null && !request.category().isBlank()) {
            product.setCategory(request.category().trim());
        }
        if (request.unit() != null && !request.unit().isBlank()) {
            product.setUnit(request.unit().trim());
        }
        product.setPrice(request.price().intValue());
        if (request.status() != null && !request.status().isBlank()) {
            product.setStatus(request.status().trim());
        }
        if (request.categoryId() != null) {
            Category cat = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục: " + request.categoryId()));
            product.setCategoryEntity(cat);
            product.setCategory(cat.getName());
        }
        if (request.brandId() != null) {
            Brand brand = brandRepository.findById(request.brandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu: " + request.brandId()));
            product.setBrandEntity(brand);
        }
        if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
            if (product.getImages().isEmpty()) {
                ProductImage img = productImageRepository.save(new ProductImage(request.imageUrl().trim(), product));
                product.getImages().add(img);
            } else {
                product.getImages().get(0).setImageUrl(request.imageUrl().trim());
            }
        }

        return toResponse(productRepository.save(product));
    }

    public void delete(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm: " + id);
        }
        productRepository.deleteById(id);
    }

    public ProductResponse getBySku(String sku) {
        Product product = productRepository.findBySkuIgnoreCase(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với SKU: " + sku));
        return toResponse(product);
    }

    public List<ProductResponse> search(String keyword, String category) {
        return productRepository.findAll().stream()
                .filter(p -> {
                    boolean matchKeyword = keyword == null || keyword.isBlank()
                            || (p.getName() != null && p.getName().toLowerCase().contains(keyword.toLowerCase()))
                            || (p.getSku() != null && p.getSku().toLowerCase().contains(keyword.toLowerCase()));
                    boolean matchCategory = category == null || category.isBlank()
                            || (p.getCategory() != null && category.equalsIgnoreCase(p.getCategory()));
                    return matchKeyword && matchCategory;
                })
                .map(this::toResponse)
                .toList();
    }
}
