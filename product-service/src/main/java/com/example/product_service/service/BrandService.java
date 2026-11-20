package com.example.product_service.service;

import com.example.product_service.entity.Brand;
import com.example.product_service.repository.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {

    private final BrandRepository repository;

    public BrandService(BrandRepository repository) {
        this.repository = repository;
    }

    public List<Brand> getAll() {
        return repository.findAll();
    }

    public Optional<Brand> getById(int id) {
        return repository.findById(id);
    }

    public Brand create(Brand brand) {
        return repository.save(brand);
    }

    public Optional<Brand> update(int id, Brand payload) {
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
