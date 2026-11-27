package com.example.order_service.controller;

import com.example.order_service.dto.OrderRequest;
import com.example.order_service.dto.OrderResponse;
import com.example.order_service.dto.OrderUpdateRequest;
import com.example.order_service.dto.ProductResponse;
import com.example.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponse> getOrders() {
        return orderService.getAll();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Integer id) {
        return orderService.getById(id);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }

    @PutMapping("/{id}")
    public OrderResponse updateOrder(
            @PathVariable Integer id,
            @Valid @RequestBody OrderUpdateRequest request) {
        return orderService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable Integer id) {
        orderService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa đơn nhập hàng ID = " + id));
    }

    @GetMapping("/product/{id}")
    public ProductResponse getProduct(@PathVariable Integer id) {
        return orderService.getProduct(id);
    }

    @GetMapping("/status/{status}")
    public List<OrderResponse> getOrdersByStatus(@PathVariable String status) {
        return orderService.getByStatus(status);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveOrder(
            @PathVariable Integer id,
            @RequestBody(required = false) Map<String, Object> payload) {
        return orderService.approveOrder(id, payload);
    }

    @PutMapping("/{id}/receive")
    public OrderResponse markOrderAsReceived(@PathVariable Integer id) {
        return orderService.markOrderAsReceived(id);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer id) {
        return orderService.cancelOrder(id);
    }
}
