package com.example.order_service.service;

import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.OrderItemRequest;
import com.example.order_service.dto.OrderItemResponse;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.dto.OrderResponse;
import com.example.order_service.dto.OrderUpdateRequest;
import com.example.order_service.dto.ProductResponse;
import com.example.order_service.entity.OrderItem;
import com.example.order_service.entity.PurchaseOrder;
import com.example.order_service.exception.ResourceNotFoundException;
import com.example.order_service.repository.OrderItemRepository;
import com.example.order_service.repository.OrderRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
                        1, 20, 390000, 7800000, 4,
                        "RECEIVED",
                        LocalDateTime.now().minusDays(2).toString()
                ));
                orderItemRepository.save(new OrderItem(1, "Mouse Logitech G102 Lightsync", 20, 390000, o1));

                PurchaseOrder o2 = orderRepository.save(new PurchaseOrder(
                        "PO-2026-102",
                        "Nhà phân phối Linh kiện GearVN",
                        2, 15, 2150000, 32250000, 4,
                        "APPROVED",
                        LocalDateTime.now().minusDays(1).toString()
                ));
                orderItemRepository.save(new OrderItem(2, "Keyboard Mechanical Keychron K2 Pro", 15, 2150000, o2));
            }
        } catch (Exception ignored) {
        }
    }

    private OrderResponse toResponse(PurchaseOrder order) {
        List<OrderItemResponse> itemResponses = order.getItems() == null
                ? List.of()
                : order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice() != null ? BigDecimal.valueOf(item.getUnitPrice()) : BigDecimal.ZERO,
                        item.getSubTotal() != null ? BigDecimal.valueOf(item.getSubTotal()) : BigDecimal.ZERO
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderCode(),
                order.getSupplierName(),
                order.getTotalAmount() != null ? BigDecimal.valueOf(order.getTotalAmount()) : BigDecimal.ZERO,
                order.getStatus(),
                order.getCreatedByUserId(),
                order.getApprovedByUserId(),
                order.getCreatedAt(),
                order.getApprovedAt(),
                order.getReceivedAt(),
                order.getCancelledAt(),
                itemResponses
        );
    }

    public List<OrderResponse> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse getById(Integer id) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + id));
        return toResponse(order);
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQuantity = 0;

        PurchaseOrder order = new PurchaseOrder();
        order.setSupplierName(
                request.supplierName() != null && !request.supplierName().isBlank()
                        ? request.supplierName().trim()
                        : "Nhà cung cấp mặc định"
        );
        order.setCreatedByUserId(request.createdByUserId() != null ? request.createdByUserId() : 4);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now().toString());

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.items()) {
            // Gọi Product Service để kiểm tra sản phẩm và lấy đơn giá
            ProductResponse product = productClient.getById(itemReq.productId());
            if (product == null || product.price() == null) {
                throw new ResourceNotFoundException("Không tìm thấy thông tin sản phẩm: " + itemReq.productId());
            }

            // Tính thành tiền từng sản phẩm
            BigDecimal amount = product.price()
                    .multiply(BigDecimal.valueOf(itemReq.quantity()));

            // Cộng dồn tổng tiền đơn hàng
            totalAmount = totalAmount.add(amount);
            totalQuantity += itemReq.quantity();

            OrderItem orderItem = new OrderItem(
                    product.id(),
                    product.name(),
                    itemReq.quantity(),
                    product.price().intValue(),
                    order
            );
            orderItems.add(orderItem);
        }

        // Cập nhật thông tin tổng hợp đơn hàng
        OrderItem firstItem = orderItems.get(0);
        order.setProductId(firstItem.getProductId());
        order.setQuantity(totalQuantity);
        order.setUnitPrice(firstItem.getUnitPrice());
        order.setTotalAmount(totalAmount.intValue());
        order.setItems(orderItems);

        // Lưu Order và OrderItem vào CSDL
        PurchaseOrder saved = orderRepository.save(order);
        if (saved.getOrderCode() == null || saved.getOrderCode().isBlank()) {
            saved.setOrderCode("PO-2026-" + saved.getId());
            saved = orderRepository.save(saved);
        }

        // Gửi thông báo đến Notification Service (không làm gián đoạn nghiệp vụ nếu service chưa bật)
        try {
            notificationClient.post()
                    .uri("/api/notifications/send")
                    .body(Map.of(
                            "title", "Đơn đặt hàng nhập kho mới " + saved.getOrderCode(),
                            "content", "Đơn nhập " + saved.getOrderCode() + " (Tổng SL: " + totalQuantity + ") đang chờ Quản lý kho phê duyệt.",
                            "type", "NEW_ORDER",
                            "referenceId", saved.getOrderCode(),
                            "recipient", "WAREHOUSE_MANAGER"
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ignored) {
        }

        return toResponse(saved);
    }

    public OrderResponse update(Integer id, OrderUpdateRequest request) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + id));

        if (request.supplierName() != null && !request.supplierName().isBlank()) {
            order.setSupplierName(request.supplierName().trim());
        }
        if (request.status() != null && !request.status().isBlank()) {
            order.setStatus(request.status().trim());
        }
        return toResponse(orderRepository.save(order));
    }

    public void delete(Integer id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy đơn hàng: " + id);
        }
        orderRepository.deleteById(id);
    }

    public ProductResponse getProduct(Integer productId) {
        return productClient.getById(productId);
    }

    public List<OrderResponse> getByStatus(String status) {
        return orderRepository.findByStatusIgnoreCase(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ResponseEntity<?> approveOrder(Integer id, Map<String, Object> payload) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + id));

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
                        "message", "Nhân viên ID = " + managerId + " không có quyền WAREHOUSE_MANAGER để duyệt đơn"
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

        return ResponseEntity.ok(toResponse(saved));
    }

    public OrderResponse markOrderAsReceived(Integer id) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + id));
        order.setStatus("RECEIVED");
        order.setReceivedAt(LocalDateTime.now().toString());
        return toResponse(orderRepository.save(order));
    }

    public ResponseEntity<?> cancelOrder(Integer id) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + id));

        if ("RECEIVED".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không thể hủy đơn hàng đã nhập kho hoàn tất"));
        }

        order.setStatus("CANCELLED");
        order.setCancelledAt(LocalDateTime.now().toString());
        return ResponseEntity.ok(toResponse(orderRepository.save(order)));
    }

    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
}
