package com.example.notification_service.controller;

import com.example.notification_service.entity.Notification;
import com.example.notification_service.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8081", "http://localhost:8082", "http://localhost:8083", "http://localhost:8084", "http://localhost:8086" })
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ==================== CRUD ====================

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable int id) {
        return notificationService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(notificationService.create(payload));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateNotification(
            @PathVariable int id,
            @RequestBody Map<String, Object> payload) {
        return notificationService.update(id, payload)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable int id) {
        if (!notificationService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("message", "Đã xóa thông báo ID = " + id));
    }

    // ==================== NGHIỆP VỤ CƠ BẢN ====================

    @GetMapping("/unread")
    public List<Notification> getUnreadNotifications() {
        return notificationService.getUnreadNotifications();
    }

    @GetMapping("/recipient/{recipient}")
    public List<Notification> getNotificationsByRecipient(@PathVariable String recipient) {
        return notificationService.getNotificationsByRecipient(recipient);
    }

    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(notificationService.create(payload));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable int id) {
        return notificationService.markAsRead(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        int total = notificationService.markAllAsRead();
        return ResponseEntity.ok(Map.of(
                "message", "Đã đánh dấu đã đọc tất cả thông báo",
                "total", total
        ));
    }
}
