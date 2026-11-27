package com.example.order_service.service;

import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.ProductResponse;
import com.example.order_service.entity.OrderItem;
import com.example.order_service.entity.PurchaseOrder;
import com.example.order_service.repository.OrderItemRepository;
import com.example.order_service.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    private final ProductClient productClient;
    private final RestClient notificationClient;
    private final RestClient userClient;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final JdbcTemplate jdbcTemplate;

    public OrderService(ProductClient productClient,
                        RestClient.Builder builder,
                        @Value("${services.notification.url}") String notificationUrl,
                        @Value("${services.user.url}") String userUrl,
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        JdbcTemplate jdbcTemplate) {
        this.productClient = productClient;
        this.notificationClient = builder.baseUrl(notificationUrl).build();
        this.userClient = builder.baseUrl(userUrl).build();
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initSeedData() {
        try {
            if (orderRepository.count() == 0) {
                jdbcTemplate.execute("ALTER TABLE orders AUTO_INCREMENT = 101");
                PurchaseOrder o1 = orderRepository.save(new PurchaseOrder(
                        "PO-2026-101",
                        "Công ty TNHH Thiết bị Số Hoàng Gia",
                        1, 20, 150000, 3000000, 4,
                        "RECEIVED",
                        LocalDateTime.now().minusDays(2).toString()
                ));
                orderItemRepository.save(new OrderItem(1, "Mouse Logitech G102", 20, 150000, o1));

                PurchaseOrder o2 = orderRepository.save(new PurchaseOrder(
                        "PO-2026-102",
                        "Nhà phân phối Linh kiện GearVN",
                        2, 15, 250000, 3750000, 4,
                        "APPROVED",
                        LocalDateTime.now().minusDays(1).toString()
                ));
                orderItemRepository.save(new OrderItem(2, "Keyboard Mechanical Keychron", 15, 250000, o2));
            }
        } catch (Exception ignored) {
        }
    }

    public List<PurchaseOrder> getAll() {
        return orderRepository.findAll();
    }

    public Optional<PurchaseOrder> getById(int id) {
        return orderRepository.findById(id);
    }

    public PurchaseOrder create(Map<String, Object> payload) {
        int productId = ((Number) payload.getOrDefault("productId", 1)).intValue();
        int quantity = ((Number) payload.getOrDefault("quantity", 10)).intValue();
        int unitPrice = ((Number) payload.getOrDefault("unitPrice", 150000)).intValue();
        String productName = "Sản phẩm #" + productId;

        try {
            ProductResponse product = productClient.getById(productId);
            if (product != null) {
                if (!payload.containsKey("unitPrice") && product.price() != null) {
                    unitPrice = product.price().intValue();
                }
                if (product.name() != null) {
                    productName = product.name();
                }
            }
        } catch (Exception ignored) {
        }

        PurchaseOrder order = new PurchaseOrder();
        order.setSupplierName((String) payload.getOrDefault("supplierName", "Nhà cung cấp mặc định"));
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(quantity * unitPrice);
        order.setCreatedByUserId(((Number) payload.getOrDefault("createdByUserId", 4)).intValue());
        order.setStatus((String) payload.getOrDefault("status", "PENDING"));
        order.setCreatedAt(LocalDateTime.now().toString());
        order.setOrderCode((String) payload.get("orderCode"));

        PurchaseOrder saved = orderRepository.save(order);
        if (saved.getOrderCode() == null || saved.getOrderCode().isBlank()) {
            saved.setOrderCode("PO-2026-" + saved.getId());
            saved = orderRepository.save(saved);
        }

        OrderItem item = orderItemRepository.save(new OrderItem(productId, productName, quantity, unitPrice, saved));
        saved.getItems().add(item);

        try {
            notificationClient.post()
                    .uri("/api/notifications/send")
                    .body(Map.of(
                            "title", "Đơn đặt hàng nhập kho mới " + saved.getOrderCode(),
                            "content", "Đơn nhập " + saved.getOrderCode() + " (SL: " + quantity + ") đang chờ Quản lý kho phê duyệt.",
                            "type", "NEW_ORDER",
                            "referenceId", saved.getOrderCode(),
                            "recipient", "WAREHOUSE_MANAGER"
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ignored) {
        }

        return saved;
    }

    public Optional<PurchaseOrder> update(int id, Map<String, Object> payload) {
        Optional<PurchaseOrder> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }

        PurchaseOrder order = orderOpt.get();
        if (payload.containsKey("supplierName")) order.setSupplierName(String.valueOf(payload.get("supplierName")));
        if (payload.containsKey("productId")) order.setProductId(((Number) payload.get("productId")).intValue());
        if (payload.containsKey("quantity")) order.setQuantity(((Number) payload.get("quantity")).intValue());
        if (payload.containsKey("unitPrice")) order.setUnitPrice(((Number) payload.get("unitPrice")).intValue());
        if (payload.containsKey("status")) order.setStatus(String.valueOf(payload.get("status")));

        order.setTotalAmount(order.getQuantity() * order.getUnitPrice());
        return Optional.of(orderRepository.save(order));
    }

    public boolean delete(int id) {
        if (!orderRepository.existsById(id)) {
            return false;
        }
        orderRepository.deleteById(id);
        return true;
    }

    public ProductResponse getProduct(Integer productId) {
        return productClient.getById(productId);
    }

    public List<PurchaseOrder> getByStatus(String status) {
        return orderRepository.findByStatusIgnoreCase(status);
    }

    public ResponseEntity<?> approveOrder(int id, Map<String, Object> payload) {
        Optional<PurchaseOrder> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PurchaseOrder order = orderOpt.get();
        int managerId = payload != null && payload.containsKey("approvedByUserId")
                ? ((Number) payload.get("approvedByUserId")).intValue()
                : 1;

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> validation = userClient.get()
                    .uri("/api/users/{id}/validate-role?requiredRole=WAREHOUSE_MANAGER", managerId)
                    .retrieve()
                    .body(Map.class);
            if (validation != null && Boolean.FALSE.equals(validation.get("valid"))) {
                return ResponseEntity.status(403).body(Map.of(
                        "error", "Nhân viên ID = " + managerId + " không có quyền WAREHOUSE_MANAGER để duyệt đơn"
                ));
            }
        } catch (Exception ignored) {
        }

        order.setStatus("APPROVED");
        order.setApprovedByUserId(managerId);
        order.setApprovedAt(LocalDateTime.now().toString());
        PurchaseOrder saved = orderRepository.save(order);

        try {
            notificationClient.post()
                    .uri("/api/notifications/send")
                    .body(Map.of(
                            "title", "Đơn nhập hàng " + saved.getOrderCode() + " đã được duyệt",
                            "content", "Đơn hàng #" + id + " đã được phê duyệt. Thủ kho chuẩn bị tiếp nhận hàng.",
                            "type", "ORDER_APPROVED",
                            "referenceId", String.valueOf(saved.getOrderCode()),
                            "recipient", "WAREHOUSE_KEEPER"
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ignored) {
        }

        return ResponseEntity.ok(saved);
    }

    public Optional<PurchaseOrder> markOrderAsReceived(int id) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus("RECEIVED");
            order.setReceivedAt(LocalDateTime.now().toString());
            return orderRepository.save(order);
        });
    }

    public ResponseEntity<?> cancelOrder(int id) {
        Optional<PurchaseOrder> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PurchaseOrder order = orderOpt.get();
        if ("RECEIVED".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Không thể hủy đơn hàng đã nhập kho hoàn tất"));
        }

        order.setStatus("CANCELLED");
        order.setCancelledAt(LocalDateTime.now().toString());
        return ResponseEntity.ok(orderRepository.save(order));
    }

    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
}
