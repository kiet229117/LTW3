package com.example.user_service.controller;

import com.example.user_service.entity.User;
import com.example.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8081", "http://localhost:8082", "http://localhost:8083", "http://localhost:8084", "http://localhost:8085" })
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ==================== CRUD ====================

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        return userService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(userService.create(payload));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable int id,
            @RequestBody Map<String, Object> payload) {
        return userService.update(id, payload)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable int id) {
        if (!userService.delete(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("message", "Đã xóa nhân viên ID = " + id));
    }

    // ==================== NGHIỆP VỤ CƠ BẢN ====================

    @GetMapping("/role/{role}")
    public List<User> getUsersByRole(@PathVariable String role) {
        return userService.getUsersByRole(role);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<User> changeUserStatus(
            @PathVariable int id,
            @RequestBody Map<String, Object> payload) {
        return userService.changeUserStatus(id, payload)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> payload) {
        return userService.login(payload);
    }

    @GetMapping("/{id}/validate-role")
    public ResponseEntity<Map<String, Object>> validateUserRole(
            @PathVariable int id,
            @RequestParam String requiredRole) {
        return userService.validateUserRole(id, requiredRole);
    }
}
