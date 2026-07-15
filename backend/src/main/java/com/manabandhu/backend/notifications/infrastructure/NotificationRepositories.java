package com.manabandhu.backend.notifications.infrastructure;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    @Query("select n from NotificationEntity n where n.userId = :userId order by n.createdAt desc")
    Page<NotificationEntity> findByUser(@Param("userId") UUID userId, Pageable pageable);

    long countByUserIdAndReadFalse(UUID userId);
}

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreferenceEntity, UUID> {
    NotificationPreferenceEntity findByUserId(UUID userId);
}

@Repository
public interface PushTokenRepository extends JpaRepository<PushTokenEntity, UUID> {
    PushTokenEntity findByUserIdAndToken(UUID userId, String token);
}
