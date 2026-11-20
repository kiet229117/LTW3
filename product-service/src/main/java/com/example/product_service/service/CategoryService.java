package com.example.product_service.service;

import com.example.product_service.entity.Category;
import com.example.product_service.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> getAll() {
        return repository.findAll();
    }

    public Optional<Category> getById(int id) {
        return repository.findById(id);
    }

    public Category create(Category category) {
        return repository.save(category);
    }

    public Optional<Category> update(int id, Category payload) {
        return repository.findById(id).map(existing -> {
            if (payload.getName() != null) existing.setName(payload.getName());
            if (payload.getDescription() != null) existing.setDescription(payload.getDescription());
            return repository.save(existing);
        });
    }

    public boolean delete(int id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
