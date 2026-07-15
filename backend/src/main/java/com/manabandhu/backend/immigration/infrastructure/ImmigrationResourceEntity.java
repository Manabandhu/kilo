package com.manabandhu.backend.immigration.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "immigration_resources")
@Getter
@Setter
@NoArgsConstructor
public class ImmigrationResourceEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID categoryId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 12000)
    private String body;

    @Column(columnDefinition = "text[]")
    private String[] tags = new String[0];

    @Column(nullable = false)
    private LocalDate versionDate;

    @Column(nullable = false)
    private java.time.Instant lastReviewedAt;

    @Column(columnDefinition = "text[]")
    private String[] sourceReferences = new String[0];

    @Column(nullable = false)
    private boolean trustedContributor;

    private UUID authorId;

    @Column(nullable = false)
    private int helpfulCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status = ResourceStatus.published;

    public enum ResourceStatus { draft, published, archived }
}
