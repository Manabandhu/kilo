package com.manabandhu.backend.immigration.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "immigration_categories")
@Getter
@Setter
@NoArgsConstructor
public class ImmigrationCategoryEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private int orderIndex;
}
