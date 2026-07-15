package com.manabandhu.backend.community.domain;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "saved_items")
public class SavedItem extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "item_type", nullable = false)
    private String itemType;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    public SavedItem() {}

    public SavedItem(UUID userId, String itemType, UUID itemId) {
        this.userId = userId;
        this.itemType = itemType;
        this.itemId = itemId;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public UUID getItemId() { return itemId; }
    public void setItemId(UUID itemId) { this.itemId = itemId; }
}
