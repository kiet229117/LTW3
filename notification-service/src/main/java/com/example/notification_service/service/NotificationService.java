package com.example.notification_service.service;

import com.example.notification_service.entity.Notification;
import com.example.notification_service.repository.NotificationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @PostConstruct
    public void initSeedData() {
        try {
            if (notificationRepository.count() == 0) {
                notificationRepository.save(new Notification(
                        "Đơn nhập hàng mới #101",
                        "Đơn đặt hàng #101 đã được tạo và đang chờ thủ kho tiếp nhận.",
                        "NEW_ORDER",
                        "ORDER-101",
                        "WAREHOUSE_KEEPER",
                        true,
                        LocalDateTime.now().minusHours(5).toString()
                ));
                notificationRepository.save(new Notification(
                        "Cảnh báo tồn kho thấp",
                        "Sản phẩm Monitor (ID: 3) tại KHO-B-KE-01 chỉ còn 3 sản phẩm (Định mức tối thiểu: 5).",
                        "LOW_STOCK_ALERT",
                        "PROD-3",
                        "PURCHASING_STAFF",
                        false,
                        LocalDateTime.now().minusHours(1).toString()
                ));
            }
        } catch (Exception ignored) {
        }
    }

    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    public Optional<Notification> getById(int id) {
        return notificationRepository.findById(id);
    }

    public Notification create(Map<String, Object> payload) {
        Notification notification = new Notification(
                String.valueOf(payload.getOrDefault("title", "Thông báo hệ thống kho")),
                String.valueOf(payload.getOrDefault("content", "")),
                String.valueOf(payload.getOrDefault("type", "SYSTEM")),
                String.valueOf(payload.getOrDefault("referenceId", "N/A")),
                String.valueOf(payload.getOrDefault("recipient", "ALL")),
                false,
                LocalDateTime.now().toString()
        );
        return notificationRepository.save(notification);
    }

    public Optional<Notification> update(int id, Map<String, Object> payload) {
        Optional<Notification> notifOpt = notificationRepository.findById(id);
        if (notifOpt.isEmpty()) {
            return Optional.empty();
        }

        Notification notif = notifOpt.get();
        if (payload.containsKey("title")) notif.setTitle(String.valueOf(payload.get("title")));
        if (payload.containsKey("content")) notif.setContent(String.valueOf(payload.get("content")));
        if (payload.containsKey("type")) notif.setType(String.valueOf(payload.get("type")));
        if (payload.containsKey("recipient")) notif.setRecipient(String.valueOf(payload.get("recipient")));
        if (payload.containsKey("isRead")) notif.setIsRead(Boolean.valueOf(String.valueOf(payload.get("isRead"))));

        return Optional.of(notificationRepository.save(notif));
    }

    public boolean delete(int id) {
        if (!notificationRepository.existsById(id)) {
            return false;
        }
        notificationRepository.deleteById(id);
        return true;
    }

    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findByIsReadFalse();
    }

    public List<Notification> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findAll().stream()
                .filter(n -> recipient.equalsIgnoreCase(n.getRecipient())
                        || "ALL".equalsIgnoreCase(n.getRecipient()))
                .toList();
    }

    public Optional<Notification> markAsRead(int id) {
        return notificationRepository.findById(id)
                .map(n -> {
                    n.setIsRead(true);
                    return notificationRepository.save(n);
                });
    }

    public int markAllAsRead() {
        List<Notification> all = notificationRepository.findAll();
        all.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(all);
        return all.size();
    }
}
