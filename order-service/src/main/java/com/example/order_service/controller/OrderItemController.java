package com.example.order_service.controller;

import com.example.order_service.entity.OrderItem;
import com.example.order_service.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderService orderService;

    public OrderItemController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderItem> getAll() {
        return orderService.getAllOrderItems();
    }

    @GetMapping("/order/{orderId}")
    public List<OrderItem> getByOrderId(@PathVariable int orderId) {
        return orderService.getOrderItemsByOrderId(orderId);
    }
}
