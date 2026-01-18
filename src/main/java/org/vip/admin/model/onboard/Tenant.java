package org.vip.admin.model.onboard;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tenants")
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 50)
    private String id;
    @Column(nullable = false)
    private String name;
    @Column(name = "application_url")
    private String applicationUrl;
    @Column(nullable = false, length = 50)
    private String plan;
    @Column(nullable = false, length = 50)
    private String region;
    @Column(length = 50)
    private String status;
    private Integer health;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
