package com.example.order_service.controller;

import com.example.order_service.entity.OrderItem;
import com.example.order_service.entity.PurchaseOrder;
import com.example.order_service.repository.OrderItemRepository;
import com.example.order_service.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public OrderItemController(OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public List<OrderItem> getAll() {
        return orderItemRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getById(@PathVariable int id) {
        return orderItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public List<OrderItem> getByOrderId(@PathVariable int orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload) {
        int orderId = ((Number) payload.getOrDefault("orderId", 101)).intValue();
        Optional<PurchaseOrder> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy đơn hàng ID = " + orderId));
        }

        int productId = ((Number) payload.getOrDefault("productId", 1)).intValue();
        String productName = String.valueOf(payload.getOrDefault("productName", "Sản phẩm #" + productId));
        int quantity = ((Number) payload.getOrDefault("quantity", 1)).intValue();
        int unitPrice = ((Number) payload.getOrDefault("unitPrice", 100000)).intValue();

        OrderItem saved = orderItemRepository.save(new OrderItem(productId, productName, quantity, unitPrice, orderOpt.get()));
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> update(@PathVariable int id, @RequestBody Map<String, Object> payload) {
        Optional<OrderItem> itemOpt = orderItemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        OrderItem item = itemOpt.get();
        if (payload.containsKey("productId")) item.setProductId(((Number) payload.get("productId")).intValue());
        if (payload.containsKey("productName")) item.setProductName(String.valueOf(payload.get("productName")));
        if (payload.containsKey("quantity")) item.setQuantity(((Number) payload.get("quantity")).intValue());
        if (payload.containsKey("unitPrice")) item.setUnitPrice(((Number) payload.get("unitPrice")).intValue());
        item.setSubTotal(item.getQuantity() * item.getUnitPrice());

        return ResponseEntity.ok(orderItemRepository.save(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable int id) {
        if (!orderItemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        orderItemRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa chi tiết đơn hàng ID = " + id));
    }
}
