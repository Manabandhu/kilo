package com.manabandhu.backend.profiles.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "saved_items")
public class SavedItemEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String itemType;

    @Column(nullable = false)
    private UUID itemId;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID v) { this.userId = v; }
    public String getItemType() { return itemType; }
    public void setItemType(String v) { this.itemType = v; }
    public UUID getItemId() { return itemId; }
    public void setItemId(UUID v) { this.itemId = v; }
}
