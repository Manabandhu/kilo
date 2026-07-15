package com.manabandhu.backend.community.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "communities")
@Getter
@Setter
@NoArgsConstructor
public class CommunityEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private String kind;

    @Column(nullable = false)
    private String category;

    private String city;

    @Column(length = 2)
    private String state;

    @Column(nullable = false)
    private int memberCount;

    private String coverImageUrl;

    @Column(columnDefinition = "uuid[]")
    private UUID[] pinnedPostIds = new UUID[0];
}
