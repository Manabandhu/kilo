package com.manabandhu.backend.community.infrastructure;

import com.manabandhu.backend.community.domain.SavedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SavedItemRepository extends JpaRepository<SavedItem, UUID> {
    Optional<SavedItem> findByUserIdAndItemTypeAndItemId(UUID userId, String itemType, UUID itemId);
    boolean existsByUserIdAndItemTypeAndItemId(UUID userId, String itemType, UUID itemId);
}
