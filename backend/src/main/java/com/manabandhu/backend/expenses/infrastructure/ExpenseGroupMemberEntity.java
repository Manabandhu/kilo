package com.manabandhu.backend.expenses.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "expense_group_members")
@Getter
@Setter
@NoArgsConstructor
public class ExpenseGroupMemberEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID groupId;

    @Column(nullable = false)
    private UUID userId;
}
