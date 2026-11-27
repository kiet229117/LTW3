package com.example.product_service.service;

import com.example.product_service.dto.CategoryRequest;
import com.example.product_service.dto.CategoryResponse;
import com.example.product_service.entity.Category;
import com.example.product_service.exception.ResourceNotFoundException;
import com.example.product_service.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    public List<CategoryResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse getById(Integer id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục: " + id));
        return toResponse(category);
    }

    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category(
                request.name().trim(),
                request.description() != null ? request.description().trim() : ""
        );
        return toResponse(repository.save(category));
    }

    public CategoryResponse update(Integer id, CategoryRequest request) {
        Category existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục: " + id));
        existing.setName(request.name().trim());
        existing.setDescription(request.description() != null ? request.description().trim() : "");
        return toResponse(repository.save(existing));
    }

    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy danh mục: " + id);
        }
        repository.deleteById(id);
    }
}
