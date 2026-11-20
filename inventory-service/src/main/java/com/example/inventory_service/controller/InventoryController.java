package com.example.inventory_service.controller;

import com.example.inventory_service.entity.Inventory;
import com.example.inventory_service.entity.InventoryTransaction;
import com.example.inventory_service.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8081", "http://localhost:8082", "http://localhost:8084", "http://localhost:8085", "http://localhost:8086" })
@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ==================== CRUD ====================

    @GetMapping
    public List<Inventory> getAllInventories() {
        return inventoryService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable int id) {
        return inventoryService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Inventory> createInventory(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(inventoryService.create(payload));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(
            @PathVariable int id,
            @RequestBody Map<String, Object> payload) {
        return inventoryService.update(id, payload)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteInventory(@PathVariable int id) {
        if (!inventoryService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("message", "Đã xóa bản ghi tồn kho ID = " + id));
    }

    // ==================== NGHIỆP VỤ CƠ BẢN ====================

    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getInventoryByProduct(@PathVariable int productId) {
        return inventoryService.getInventoryByProduct(productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/low-stock")
    public List<Inventory> getLowStockItems() {
        return inventoryService.getLowStockItems();
    }

    @GetMapping("/transactions")
    public List<InventoryTransaction> getTransactions() {
        return inventoryService.getTransactions();
    }

    @PostMapping("/inbound")
    public ResponseEntity<Map<String, Object>> addInboundStock(@RequestBody Map<String, Object> payload) {
        return inventoryService.addInboundStock(payload);
    }

    @PostMapping("/adjust")
    public ResponseEntity<Map<String, Object>> adjustInventory(@RequestBody Map<String, Object> payload) {
        return inventoryService.adjustInventory(payload);
    }
}
