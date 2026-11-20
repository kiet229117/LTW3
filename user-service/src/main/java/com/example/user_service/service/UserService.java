package com.example.user_service.service;

import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initSeedData() {
        try {
            if (userRepository.count() == 0) {
                userRepository.save(new User("manager01", "Lê Hoàng Nam", "WAREHOUSE_MANAGER", "nam.le@warehouse.vn", "KHO-A", "ACTIVE"));
                userRepository.save(new User("keeper01", "Nguyễn Văn A", "WAREHOUSE_KEEPER", "a.nguyen@warehouse.vn", "KHO-A", "ACTIVE"));
                userRepository.save(new User("keeper02", "Trần Thị B", "WAREHOUSE_KEEPER", "b.tran@warehouse.vn", "KHO-B", "ACTIVE"));
                userRepository.save(new User("purchasing01", "Phạm Minh Tuấn", "PURCHASING_STAFF", "tuan.pham@warehouse.vn", "ALL", "ACTIVE"));
            }
        } catch (Exception ignored) {
        }
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> getById(int id) {
        return userRepository.findById(id);
    }

    public User create(Map<String, Object> payload) {
        User newUser = new User();
        newUser.setUsername(payload.get("username") != null ? String.valueOf(payload.get("username")) : "user_" + System.currentTimeMillis());
        newUser.setFullName(payload.get("fullName") != null ? String.valueOf(payload.get("fullName")) : "Nhân viên kho");
        newUser.setRole((String) payload.getOrDefault("role", "WAREHOUSE_KEEPER"));
        newUser.setEmail((String) payload.get("email"));
        newUser.setWarehouse((String) payload.getOrDefault("warehouse", "KHO-A"));
        newUser.setStatus((String) payload.getOrDefault("status", "ACTIVE"));

        User saved = userRepository.save(newUser);
        boolean needsUpdate = false;
        if (!payload.containsKey("username")) {
            saved.setUsername("user0" + saved.getId());
            needsUpdate = true;
        }
        if (!payload.containsKey("fullName")) {
            saved.setFullName("Nhân viên kho " + saved.getId());
            needsUpdate = true;
        }
        if (saved.getEmail() == null || saved.getEmail().isBlank()) {
            saved.setEmail("user" + saved.getId() + "@warehouse.vn");
            needsUpdate = true;
        }
        if (needsUpdate) {
            saved = userRepository.save(saved);
        }

        return saved;
    }

    public Optional<User> update(int id, Map<String, Object> payload) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        if (payload.containsKey("username")) user.setUsername(String.valueOf(payload.get("username")));
        if (payload.containsKey("fullName")) user.setFullName(String.valueOf(payload.get("fullName")));
        if (payload.containsKey("role")) user.setRole(String.valueOf(payload.get("role")));
        if (payload.containsKey("email")) user.setEmail(String.valueOf(payload.get("email")));
        if (payload.containsKey("warehouse")) user.setWarehouse(String.valueOf(payload.get("warehouse")));
        if (payload.containsKey("status")) user.setStatus(String.valueOf(payload.get("status")));

        return Optional.of(userRepository.save(user));
    }

    public boolean delete(int id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findByRoleIgnoreCase(role);
    }

    public Optional<User> changeUserStatus(int id, Map<String, Object> payload) {
        return userRepository.findById(id).map(user -> {
            String newStatus = String.valueOf(payload.getOrDefault("status", "INACTIVE")).toUpperCase();
            user.setStatus(newStatus);
            return userRepository.save(user);
        });
    }

    public ResponseEntity<Map<String, Object>> login(Map<String, Object> payload) {
        String username = String.valueOf(payload.getOrDefault("username", ""));
        return userRepository.findByUsernameIgnoreCase(username)
                .map(u -> {
                    if (!"ACTIVE".equalsIgnoreCase(u.getStatus())) {
                        return ResponseEntity.status(403).body(Map.<String, Object>of(
                                "error", "Tài khoản đã bị khóa (INACTIVE)"
                        ));
                    }
                    return ResponseEntity.ok(Map.of(
                            "message", "Đăng nhập thành công",
                            "user", u,
                            "permissions", getPermissionsForRole(u.getRole())
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of(
                        "error", "Không tìm thấy tài khoản: " + username
                )));
    }

    public ResponseEntity<Map<String, Object>> validateUserRole(int id, String requiredRole) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                    "valid", false,
                    "reason", "Không tồn tại nhân viên có ID = " + id
            ));
        }

        User user = userOpt.get();
        String actualRole = user.getRole();
        boolean isActive = "ACTIVE".equalsIgnoreCase(user.getStatus());
        boolean hasRole = requiredRole.equalsIgnoreCase(actualRole) || "WAREHOUSE_MANAGER".equalsIgnoreCase(actualRole);

        return ResponseEntity.ok(Map.of(
                "valid", isActive && hasRole,
                "user", user,
                "requiredRole", requiredRole,
                "actualRole", actualRole
        ));
    }

    private List<String> getPermissionsForRole(String role) {
        return switch (role.toUpperCase()) {
            case "WAREHOUSE_MANAGER" -> List.of("CREATE_ORDER", "APPROVE_ORDER", "CREATE_GRN", "COMPLETE_GRN", "UPDATE_INVENTORY");
            case "WAREHOUSE_KEEPER" -> List.of("CREATE_GRN", "COMPLETE_GRN", "UPDATE_INVENTORY");
            case "PURCHASING_STAFF" -> List.of("CREATE_ORDER", "VIEW_LOW_STOCK");
            default -> List.of("VIEW_ONLY");
        };
    }
}
