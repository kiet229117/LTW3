package com.example.order_service.controller;

import com.example.order_service.entity.OrderItem;
import com.example.order_service.repository.OrderItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemRepository orderItemRepository;

    public OrderItemController(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping
    public List<OrderItem> getAll() {
        return orderItemRepository.findAll();
    }

    @GetMapping("/order/{orderId}")
    public List<OrderItem> getByOrderId(@PathVariable Integer orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
}
