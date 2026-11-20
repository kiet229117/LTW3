package com.example.notification_service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String content;

    @Column(length = 50)
    private String type;

    @Column(length = 100)
    private String referenceId;

    @Column(length = 100)
    private String recipient;

    @JsonProperty("isRead")
    private Boolean isRead;

    private String createdAt;

    public Notification() {
    }

    public Notification(String title, String content, String type, String referenceId,
                        String recipient, Boolean isRead, String createdAt) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.referenceId = referenceId;
        this.recipient = recipient;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    @JsonProperty("isRead")
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
