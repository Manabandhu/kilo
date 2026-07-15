package com.manabandhu.backend.profiles.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedItemRepository extends JpaRepository<SavedItemEntity, UUID> {
    boolean existsByUserIdAndItemTypeAndItemId(UUID userId, String itemType, UUID itemId);
    void deleteByUserIdAndItemTypeAndItemId(UUID userId, String itemType, UUID itemId);
}
