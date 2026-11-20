package com.example.receiving_service.controller;

import com.example.receiving_service.entity.Receipt;
import com.example.receiving_service.service.ReceivingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8081", "http://localhost:8082", "http://localhost:8083", "http://localhost:8085", "http://localhost:8086" })
@RestController
@RequestMapping("/api/receipts")
public class ReceivingController {

    private final ReceivingService receivingService;

    public ReceivingController(ReceivingService receivingService) {
        this.receivingService = receivingService;
    }

    // ==================== CRUD ====================

    @GetMapping
    public List<Receipt> getAllReceipts() {
        return receivingService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Receipt> getReceiptById(@PathVariable int id) {
        return receivingService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Receipt> createReceipt(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(receivingService.create(payload));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReceipt(
            @PathVariable int id,
            @RequestBody Map<String, Object> payload) {
        return receivingService.update(id, payload);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteReceipt(@PathVariable int id) {
        if (!receivingService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("message", "Đã xóa phiếu nhập kho ID = " + id));
    }

    // ==================== NGHIỆP VỤ CƠ BẢN ====================

    @GetMapping("/status/{status}")
    public List<Receipt> getReceiptsByStatus(@PathVariable String status) {
        return receivingService.getReceiptsByStatus(status);
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getPendingOrdersFromOrderService() {
        return receivingService.getPendingOrdersFromOrderService();
    }

    @PutMapping("/{id}/inspect")
    public ResponseEntity<?> inspectGoods(
            @PathVariable int id,
            @RequestBody Map<String, Object> payload) {
        return receivingService.inspectGoods(id, payload);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completeReceipt(@PathVariable int id) {
        return receivingService.completeReceipt(id);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReceipt(@PathVariable int id) {
        return receivingService.cancelReceipt(id);
    }
}
