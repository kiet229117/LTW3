package com.example.inventory_service.service;

import com.example.inventory_service.entity.Inventory;
import com.example.inventory_service.entity.InventoryTransaction;
import com.example.inventory_service.repository.InventoryRepository;
import com.example.inventory_service.repository.InventoryTransactionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryService {

    private final RestClient productClient;
    private final RestClient notificationClient;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;

    public InventoryService(RestClient.Builder builder,
                            InventoryRepository inventoryRepository,
                            InventoryTransactionRepository transactionRepository) {
        this.productClient = builder.baseUrl("http://localhost:8081").build();
        this.notificationClient = builder.baseUrl("http://localhost:8085").build();
        this.inventoryRepository = inventoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostConstruct
    public void initSeedData() {
        try {
            if (inventoryRepository.count() == 0) {
                String now = LocalDateTime.now().toString();
                inventoryRepository.save(new Inventory(1, "KHO-A-KE-01", 15, 10, now));
                inventoryRepository.save(new Inventory(2, "KHO-A-KE-02", 5, 10, now));
                inventoryRepository.save(new Inventory(3, "KHO-B-KE-01", 3, 5, now));
            }
        } catch (Exception ignored) {
        }
    }

    public List<Inventory> getAll() {
        return inventoryRepository.findAll();
    }

    public Optional<Inventory> getById(int id) {
        return inventoryRepository.findById(id);
    }

    public Inventory create(Map<String, Object> payload) {
        Inventory inv = new Inventory();
        inv.setLocation((String) payload.getOrDefault("location", "KHO-A-KE-01"));
        inv.setQuantity(((Number) payload.getOrDefault("quantity", 0)).intValue());
        inv.setMinStockLevel(((Number) payload.getOrDefault("minStockLevel", 5)).intValue());
        inv.setUpdatedAt(LocalDateTime.now().toString());
        inv.setProductId(payload.containsKey("productId") ? ((Number) payload.get("productId")).intValue() : 1);

        return inventoryRepository.save(inv);
    }

    public Optional<Inventory> update(int id, Map<String, Object> payload) {
        Optional<Inventory> invOpt = inventoryRepository.findById(id);
        if (invOpt.isEmpty()) {
            return Optional.empty();
        }

        Inventory inv = invOpt.get();
        if (payload.containsKey("productId")) inv.setProductId(((Number) payload.get("productId")).intValue());
        if (payload.containsKey("location")) inv.setLocation(String.valueOf(payload.get("location")));
        if (payload.containsKey("quantity")) inv.setQuantity(((Number) payload.get("quantity")).intValue());
        if (payload.containsKey("minStockLevel")) inv.setMinStockLevel(((Number) payload.get("minStockLevel")).intValue());
        inv.setUpdatedAt(LocalDateTime.now().toString());

        return Optional.of(inventoryRepository.save(inv));
    }

    public boolean delete(int id) {
        if (!inventoryRepository.existsById(id)) {
            return false;
        }
        inventoryRepository.deleteById(id);
        return true;
    }

    public Optional<Map<String, Object>> getInventoryByProduct(int productId) {
        return inventoryRepository.findFirstByProductId(productId)
                .map(inv -> {
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("id", inv.getId());
                    result.put("productId", inv.getProductId());
                    result.put("location", inv.getLocation());
                    result.put("quantity", inv.getQuantity());
                    result.put("minStockLevel", inv.getMinStockLevel());
                    result.put("updatedAt", inv.getUpdatedAt());
                    try {
                        Object productInfo = productClient.get()
                                .uri("/api/products/{id}", productId)
                                .retrieve()
                                .body(Object.class);
                        result.put("product", productInfo);
                    } catch (Exception ignored) {
                    }
                    return result;
                });
    }

    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findAll().stream()
                .filter(inv -> inv.getQuantity() <= inv.getMinStockLevel())
                .toList();
    }

    public List<InventoryTransaction> getTransactions() {
        return transactionRepository.findAll();
    }

    public ResponseEntity<Map<String, Object>> addInboundStock(Map<String, Object> payload) {
        int productId = ((Number) payload.getOrDefault("productId", 0)).intValue();
        int quantityToAdd = ((Number) payload.getOrDefault("quantity", 0)).intValue();
        String location = (String) payload.getOrDefault("location", "KHO-A-KE-01");
        String referenceId = String.valueOf(payload.getOrDefault("referenceId", "GRN-MANUAL"));

        if (productId <= 0 || quantityToAdd <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "productId và quantity phải lớn hơn 0"));
        }

        Optional<Inventory> existingOpt = inventoryRepository.findFirstByProductId(productId);
        Inventory targetInventory;
        if (existingOpt.isPresent()) {
            targetInventory = existingOpt.get();
            targetInventory.setQuantity(targetInventory.getQuantity() + quantityToAdd);
            targetInventory.setLocation(location);
            targetInventory.setUpdatedAt(LocalDateTime.now().toString());
        } else {
            targetInventory = new Inventory(productId, location, quantityToAdd, 5, LocalDateTime.now().toString());
        }
        targetInventory = inventoryRepository.save(targetInventory);

        InventoryTransaction tx = new InventoryTransaction(
                productId,
                "INBOUND",
                quantityToAdd,
                referenceId,
                location,
                LocalDateTime.now().toString(),
                targetInventory
        );
        tx = transactionRepository.save(tx);

        try {
            notificationClient.post()
                    .uri("/api/notifications/send")
                    .body(Map.of(
                            "title", "Cập nhật tồn kho thành công",
                            "content", "Đã cộng +" + quantityToAdd + " sản phẩm (ID: " + productId + ") vào " + location + " theo phiếu " + referenceId,
                            "type", "INVENTORY_UPDATED",
                            "referenceId", referenceId,
                            "recipient", "WAREHOUSE_MANAGER"
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ignored) {
        }

        return ResponseEntity.ok(Map.of(
                "message", "Đã cộng tồn kho thành công",
                "inventory", targetInventory,
                "transaction", tx
        ));
    }

    public ResponseEntity<Map<String, Object>> adjustInventory(Map<String, Object> payload) {
        int productId = ((Number) payload.getOrDefault("productId", 0)).intValue();
        int actualQuantity = ((Number) payload.getOrDefault("actualQuantity", -1)).intValue();
        String reason = String.valueOf(payload.getOrDefault("reason", "Kiểm kê định kỳ"));

        if (productId <= 0 || actualQuantity < 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "productId và actualQuantity không hợp lệ"));
        }

        Optional<Inventory> existingOpt = inventoryRepository.findFirstByProductId(productId);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Inventory inv = existingOpt.get();
        int oldQty = inv.getQuantity();
        int diff = actualQuantity - oldQty;
        inv.setQuantity(actualQuantity);
        inv.setUpdatedAt(LocalDateTime.now().toString());
        inv = inventoryRepository.save(inv);

        InventoryTransaction tx = new InventoryTransaction(
                productId,
                "ADJUSTMENT",
                diff,
                reason,
                inv.getLocation(),
                LocalDateTime.now().toString(),
                inv
        );
        tx = transactionRepository.save(tx);

        return ResponseEntity.ok(Map.of(
                "message", "Đã điều chỉnh tồn kho theo kết quả kiểm kê",
                "oldQuantity", oldQty,
                "actualQuantity", actualQuantity,
                "difference", diff,
                "inventory", inv,
                "transaction", tx
        ));
    }
}
