package com.example.order_service.controller;

import com.example.order_service.entity.PurchaseOrder;
import com.example.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8081", "http://localhost:8083", "http://localhost:8084", "http://localhost:8085", "http://localhost:8086" })
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ==================== CRUD ====================

    @GetMapping
    public List<PurchaseOrder> getOrders() {
        return orderService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getOrderById(@PathVariable int id) {
        return orderService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> createOrder(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(orderService.create(payload));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrder> updateOrder(
            @PathVariable int id,
            @RequestBody Map<String, Object> payload) {
        return orderService.update(id, payload)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable int id) {
        if (!orderService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("message", "Đã xóa đơn nhập hàng ID = " + id));
    }

    // ==================== NGHIỆP VỤ CƠ BẢN ====================

    @GetMapping("/product/{id}")
    public String getProduct(@PathVariable int id) {
        return orderService.getProductFromProductService(id);
    }

    @GetMapping("/status/{status}")
    public List<PurchaseOrder> getOrdersByStatus(@PathVariable String status) {
        return orderService.getByStatus(status);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveOrder(
            @PathVariable int id,
            @RequestBody(required = false) Map<String, Object> payload) {
        return orderService.approveOrder(id, payload);
    }

    @PutMapping("/{id}/receive")
    public ResponseEntity<PurchaseOrder> markOrderAsReceived(@PathVariable int id) {
        return orderService.markOrderAsReceived(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable int id) {
        return orderService.cancelOrder(id);
    }
}
