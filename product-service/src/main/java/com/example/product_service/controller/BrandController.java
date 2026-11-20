package com.example.product_service.controller;

import com.example.product_service.entity.Brand;
import com.example.product_service.service.BrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService service;

    public BrandController(BrandService service) {
        this.service = service;
    }

    @GetMapping
    public List<Brand> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brand> getById(@PathVariable int id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Brand> create(@RequestBody Brand brand) {
        return ResponseEntity.ok(service.create(brand));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Brand> update(@PathVariable int id, @RequestBody Brand brand) {
        return service.update(id, brand)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable int id) {
        if (!service.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("message", "Đã xóa thương hiệu ID = " + id));
    }
}
