package com.example.inventory_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "inventory_transactions")
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer productId;

    @Column(length = 50)
    private String type;

    private Integer quantityChange;
    private String referenceId;
    private String location;
    private String createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    @JsonIgnore
    private Inventory inventory;

    public InventoryTransaction() {
    }

    public InventoryTransaction(Integer productId, String type, Integer quantityChange,
                                String referenceId, String location, String createdAt, Inventory inventory) {
        this.productId = productId;
        this.type = type;
        this.quantityChange = quantityChange;
        this.referenceId = referenceId;
        this.location = location;
        this.createdAt = createdAt;
        this.inventory = inventory;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getQuantityChange() { return quantityChange; }
    public void setQuantityChange(Integer quantityChange) { this.quantityChange = quantityChange; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Inventory getInventory() { return inventory; }
    public void setInventory(Inventory inventory) { this.inventory = inventory; }
}
