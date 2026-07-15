package com.manabandhu.backend.moderation.infrastructure;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ModerationActionRepository extends JpaRepository<ModerationActionEntity, UUID> {
}

@Repository
public interface ModerationNoteRepository extends JpaRepository<ModerationNoteEntity, UUID> {
    java.util.List<ModerationNoteEntity> findByTargetUserId(UUID targetUserId);
}

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {

    @Query("select a from AuditLogEntity a order by a.createdAt desc")
    Page<AuditLogEntity> recent(Pageable pageable);
}
