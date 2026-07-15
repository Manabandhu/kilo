package com.manabandhu.backend.immigration.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImmigrationQuestionRepository extends JpaRepository<ImmigrationQuestionEntity, UUID> {
    java.util.List<ImmigrationQuestionEntity> findByResourceId(UUID resourceId);
}
