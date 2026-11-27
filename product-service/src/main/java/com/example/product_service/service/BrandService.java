package com.example.product_service.service;

import com.example.product_service.dto.BrandRequest;
import com.example.product_service.dto.BrandResponse;
import com.example.product_service.entity.Brand;
import com.example.product_service.exception.ResourceNotFoundException;
import com.example.product_service.repository.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    private final BrandRepository repository;

    public BrandService(BrandRepository repository) {
        this.repository = repository;
    }

    private BrandResponse toResponse(Brand brand) {
        return new BrandResponse(
                brand.getId(),
                brand.getName(),
                brand.getDescription()
        );
    }

    public List<BrandResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public BrandResponse getById(Integer id) {
        Brand brand = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu: " + id));
        return toResponse(brand);
    }

    public BrandResponse create(BrandRequest request) {
        Brand brand = new Brand(
                request.name().trim(),
                request.description() != null ? request.description().trim() : ""
        );
        return toResponse(repository.save(brand));
    }

    public BrandResponse update(Integer id, BrandRequest request) {
        Brand existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu: " + id));
        existing.setName(request.name().trim());
        existing.setDescription(request.description() != null ? request.description().trim() : "");
        return toResponse(repository.save(existing));
    }

    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy thương hiệu: " + id);
        }
        repository.deleteById(id);
    }
}
