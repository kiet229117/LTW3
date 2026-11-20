package com.example.receiving_service.service;

import com.example.receiving_service.entity.Receipt;
import com.example.receiving_service.repository.ReceiptRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReceivingService {

    private final RestClient orderClient;
    private final RestClient inventoryClient;
    private final RestClient notificationClient;
    private final RestClient userClient;
    private final ReceiptRepository receiptRepository;

    public ReceivingService(RestClient.Builder builder, ReceiptRepository receiptRepository) {
        this.orderClient = builder.baseUrl("http://localhost:8082").build();
        this.inventoryClient = builder.baseUrl("http://localhost:8083").build();
        this.notificationClient = builder.baseUrl("http://localhost:8085").build();
        this.userClient = builder.baseUrl("http://localhost:8086").build();
        this.receiptRepository = receiptRepository;
    }

    @PostConstruct
    public void initSeedData() {
        try {
            if (receiptRepository.count() == 0) {
                receiptRepository.save(new Receipt(
                        "GRN-2026-001", 101, 1, 20, 20, 0,
                        "KHO-A-KE-01", 2, "Thủ kho Nguyễn Văn A",
                        "COMPLETED", LocalDateTime.now().minusDays(1).toString()
                ));
                receiptRepository.save(new Receipt(
                        "GRN-2026-002", 102, 2, 15, 15, 1,
                        "KHO-A-KE-02", 3, "Thủ kho Trần Thị B",
                        "INSPECTING", LocalDateTime.now().toString()
                ));
            }
        } catch (Exception ignored) {
        }
    }

    public List<Receipt> getAll() {
        return receiptRepository.findAll();
    }

    public Optional<Receipt> getById(int id) {
        return receiptRepository.findById(id);
    }

    public Receipt create(Map<String, Object> payload) {
        int productId = ((Number) payload.getOrDefault("productId", 1)).intValue();
        int orderedQty = ((Number) payload.getOrDefault("orderedQty", 10)).intValue();
        int receivedQty = ((Number) payload.getOrDefault("receivedQty", orderedQty)).intValue();
        int rejectedQty = ((Number) payload.getOrDefault("rejectedQty", 0)).intValue();
        String location = (String) payload.getOrDefault("location", "KHO-A-KE-01");
        int receivedByUserId = ((Number) payload.getOrDefault("receivedByUserId", 2)).intValue();
        String receivedBy = (String) payload.getOrDefault("receivedBy", "Thủ kho");

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> userInfo = userClient.get()
                    .uri("/api/users/{id}", receivedByUserId)
                    .retrieve()
                    .body(Map.class);
            if (userInfo != null && userInfo.get("fullName") != null) {
                receivedBy = userInfo.get("fullName") + " (" + userInfo.get("role") + ")";
            }
        } catch (Exception ignored) {
        }

        Receipt receipt = new Receipt();
        receipt.setGrnCode((String) payload.get("grnCode"));
        if (payload.containsKey("orderId")) {
            receipt.setOrderId(((Number) payload.get("orderId")).intValue());
        }
        receipt.setProductId(productId);
        receipt.setOrderedQty(orderedQty);
        receipt.setReceivedQty(receivedQty);
        receipt.setRejectedQty(rejectedQty);
        receipt.setLocation(location);
        receipt.setReceivedByUserId(receivedByUserId);
        receipt.setReceivedBy(receivedBy);
        receipt.setStatus("INSPECTING");
        receipt.setReceivedDate(LocalDateTime.now().toString());

        Receipt saved = receiptRepository.save(receipt);
        boolean needsUpdate = false;
        if (saved.getGrnCode() == null || saved.getGrnCode().isBlank()) {
            saved.setGrnCode(String.format("GRN-2026-%03d", saved.getId()));
            needsUpdate = true;
        }
        if (saved.getOrderId() == null) {
            saved.setOrderId(100 + saved.getId());
            needsUpdate = true;
        }
        if (needsUpdate) {
            saved = receiptRepository.save(saved);
        }

        return saved;
    }

    public ResponseEntity<?> update(int id, Map<String, Object> payload) {
        Optional<Receipt> receiptOpt = receiptRepository.findById(id);
        if (receiptOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Receipt receipt = receiptOpt.get();
        if ("COMPLETED".equals(receipt.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Không thể chỉnh sửa phiếu nhập đã hoàn tất (COMPLETED)"));
        }

        if (payload.containsKey("productId")) receipt.setProductId(((Number) payload.get("productId")).intValue());
        if (payload.containsKey("orderedQty")) receipt.setOrderedQty(((Number) payload.get("orderedQty")).intValue());
        if (payload.containsKey("receivedQty")) receipt.setReceivedQty(((Number) payload.get("receivedQty")).intValue());
        if (payload.containsKey("rejectedQty")) receipt.setRejectedQty(((Number) payload.get("rejectedQty")).intValue());
        if (payload.containsKey("location")) receipt.setLocation(String.valueOf(payload.get("location")));
        if (payload.containsKey("receivedBy")) receipt.setReceivedBy(String.valueOf(payload.get("receivedBy")));

        return ResponseEntity.ok(receiptRepository.save(receipt));
    }

    public boolean delete(int id) {
        if (!receiptRepository.existsById(id)) {
            return false;
        }
        receiptRepository.deleteById(id);
        return true;
    }

    public List<Receipt> getReceiptsByStatus(String status) {
        return receiptRepository.findByStatusIgnoreCase(status);
    }

    public ResponseEntity<?> getPendingOrdersFromOrderService() {
        try {
            Object orders = orderClient.get()
                    .uri("/api/orders")
                    .retrieve()
                    .body(Object.class);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                    "error", "Không thể kết nối tới order-service (port 8082)",
                    "details", e.getMessage()
            ));
        }
    }

    public ResponseEntity<?> inspectGoods(int id, Map<String, Object> payload) {
        Optional<Receipt> receiptOpt = receiptRepository.findById(id);
        if (receiptOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Receipt receipt = receiptOpt.get();
        if (!"INSPECTING".equals(receipt.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Chỉ có thể cập nhật kiểm đếm khi phiếu ở trạng thái INSPECTING"));
        }

        if (payload.containsKey("receivedQty")) receipt.setReceivedQty(((Number) payload.get("receivedQty")).intValue());
        if (payload.containsKey("rejectedQty")) receipt.setRejectedQty(((Number) payload.get("rejectedQty")).intValue());
        if (payload.containsKey("location")) receipt.setLocation(String.valueOf(payload.get("location")));
        if (payload.containsKey("note")) receipt.setNote(String.valueOf(payload.get("note")));

        return ResponseEntity.ok(receiptRepository.save(receipt));
    }

    public ResponseEntity<Map<String, Object>> completeReceipt(int id) {
        Optional<Receipt> receiptOpt = receiptRepository.findById(id);
        if (receiptOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Receipt receipt = receiptOpt.get();
        if ("COMPLETED".equals(receipt.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phiếu nhập kho này đã được hoàn tất trước đó"));
        }

        int orderId = receipt.getOrderId();
        int productId = receipt.getProductId();
        int receivedQty = receipt.getReceivedQty();
        int rejectedQty = receipt.getRejectedQty();
        int acceptedQty = Math.max(0, receivedQty - rejectedQty);
        String location = receipt.getLocation();
        String grnCode = receipt.getGrnCode();

        receipt.setStatus("COMPLETED");
        receipt.setCompletedAt(LocalDateTime.now().toString());
        Receipt savedReceipt = receiptRepository.save(receipt);

        Map<String, Object> response = new HashMap<>();
        response.put("receipt", savedReceipt);
        response.put("acceptedQty", acceptedQty);

        try {
            Object inventoryResult = inventoryClient.post()
                    .uri("/api/inventories/inbound")
                    .body(Map.of(
                            "productId", productId,
                            "quantity", acceptedQty,
                            "location", location,
                            "referenceId", grnCode
                    ))
                    .retrieve()
                    .body(Object.class);
            response.put("inventorySync", inventoryResult);
        } catch (Exception e) {
            response.put("inventorySync", "Chưa đồng bộ được sang inventory-service: " + e.getMessage());
        }

        try {
            Object orderResult = orderClient.put()
                    .uri("/api/orders/{id}/receive", orderId)
                    .retrieve()
                    .body(Object.class);
            response.put("orderSync", orderResult);
        } catch (Exception e) {
            response.put("orderSync", "Chưa đồng bộ trạng thái đơn hàng sang order-service: " + e.getMessage());
        }

        try {
            notificationClient.post()
                    .uri("/api/notifications/send")
                    .body(Map.of(
                            "title", "Hoàn tất phiếu nhập kho " + grnCode,
                            "content", "Phiếu nhập " + grnCode + " (Đơn #" + orderId + ") đã kiểm kê xong. Thực nhập: " + acceptedQty + " sản phẩm.",
                            "type", "GRN_COMPLETED",
                            "referenceId", grnCode,
                            "recipient", "PURCHASING_AND_WAREHOUSE"
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ignored) {
        }

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> cancelReceipt(int id) {
        Optional<Receipt> receiptOpt = receiptRepository.findById(id);
        if (receiptOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Receipt receipt = receiptOpt.get();
        if ("COMPLETED".equals(receipt.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Không thể hủy phiếu nhập kho đã hoàn tất"));
        }

        receipt.setStatus("CANCELLED");
        receipt.setCancelledAt(LocalDateTime.now().toString());
        return ResponseEntity.ok(receiptRepository.save(receipt));
    }
}
