package com.example.receiving_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 50)
    private String grnCode;

    private Integer orderId;
    private Integer productId;
    private Integer orderedQty;
    private Integer receivedQty;
    private Integer rejectedQty;

    @Column(length = 100)
    private String location;

    private Integer receivedByUserId;
    private String receivedBy;

    @Column(length = 30)
    private String status;

    @Column(length = 500)
    private String note;

    private String receivedDate;
    private String completedAt;
    private String cancelledAt;

    public Receipt() {
    }

    public Receipt(String grnCode, Integer orderId, Integer productId, Integer orderedQty,
                   Integer receivedQty, Integer rejectedQty, String location,
                   Integer receivedByUserId, String receivedBy, String status, String receivedDate) {
        this.grnCode = grnCode;
        this.orderId = orderId;
        this.productId = productId;
        this.orderedQty = orderedQty;
        this.receivedQty = receivedQty;
        this.rejectedQty = rejectedQty;
        this.location = location;
        this.receivedByUserId = receivedByUserId;
        this.receivedBy = receivedBy;
        this.status = status;
        this.receivedDate = receivedDate;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getGrnCode() { return grnCode; }
    public void setGrnCode(String grnCode) { this.grnCode = grnCode; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public Integer getOrderedQty() { return orderedQty; }
    public void setOrderedQty(Integer orderedQty) { this.orderedQty = orderedQty; }

    public Integer getReceivedQty() { return receivedQty; }
    public void setReceivedQty(Integer receivedQty) { this.receivedQty = receivedQty; }

    public Integer getRejectedQty() { return rejectedQty; }
    public void setRejectedQty(Integer rejectedQty) { this.rejectedQty = rejectedQty; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getReceivedByUserId() { return receivedByUserId; }
    public void setReceivedByUserId(Integer receivedByUserId) { this.receivedByUserId = receivedByUserId; }

    public String getReceivedBy() { return receivedBy; }
    public void setReceivedBy(String receivedBy) { this.receivedBy = receivedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getReceivedDate() { return receivedDate; }
    public void setReceivedDate(String receivedDate) { this.receivedDate = receivedDate; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public String getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(String cancelledAt) { this.cancelledAt = cancelledAt; }
}
