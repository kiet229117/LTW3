package com.example.user_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(length = 50)
    private String role;

    @Column(length = 150)
    private String email;

    @Column(length = 50)
    private String warehouse;

    @Column(length = 30)
    private String status;

    public User() {
    }

    public User(String username, String fullName, String role, String email, String warehouse, String status) {
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.warehouse = warehouse;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWarehouse() { return warehouse; }
    public void setWarehouse(String warehouse) { this.warehouse = warehouse; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
