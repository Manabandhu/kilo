package com.manabandhu.backend.expenses.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseGroupRepository extends JpaRepository<ExpenseGroupEntity, UUID> {
}

@Repository
public interface ExpenseGroupMemberRepository extends JpaRepository<ExpenseGroupMemberEntity, UUID> {
    boolean existsByGroupIdAndUserId(UUID groupId, UUID userId);
}

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, UUID> {
    java.util.List<ExpenseEntity> findByGroupId(UUID groupId);
}

@Repository
public interface SettlementRepository extends JpaRepository<SettlementEntity, UUID> {
    java.util.List<SettlementEntity> findByGroupId(UUID groupId);
}
