package com.manabandhu.backend.expenses.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
public class ExpenseEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID groupId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String currency;

    @Column(columnDefinition = "uuid[]")
    private UUID[] paidByIds;

    @Column(nullable = false)
    private String splitStrategy;

    @Column(columnDefinition = "jsonb")
    private String splits; // [{userId, amount|percent|shares}]

    @Column(nullable = false)
    private LocalDate date;

    private String category;

    private String receiptUrl;

    @Column(nullable = false)
    private UUID createdById;
}
