package com.example.inventory_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer productId;

    @Column(length = 100)
    private String location;

    @Column(nullable = false)
    private Integer quantity;

    private Integer minStockLevel;
    private String updatedAt;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<InventoryTransaction> transactions = new ArrayList<>();

    public Inventory() {
    }

    public Inventory(Integer productId, String location, Integer quantity, Integer minStockLevel, String updatedAt) {
        this.productId = productId;
        this.location = location;
        this.quantity = quantity;
        this.minStockLevel = minStockLevel;
        this.updatedAt = updatedAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public List<InventoryTransaction> getTransactions() { return transactions; }
    public void setTransactions(List<InventoryTransaction> transactions) { this.transactions = transactions; }
}
