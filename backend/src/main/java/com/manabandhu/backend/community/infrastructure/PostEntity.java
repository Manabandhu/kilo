package com.manabandhu.backend.community.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class PostEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID communityId;

    @Column(nullable = false)
    private UUID authorId;

    private String title;

    @Column(nullable = false, length = 8000)
    private String body;

    @Column(columnDefinition = "text[]")
    private String[] tags = new String[0];

    @Column(nullable = false)
    private boolean pinned;

    @Column(columnDefinition = "jsonb")
    private String reactions; // {like,helpful,celebrate,support}

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status = PostStatus.published;

    @Column
    private UUID createdBy;

    @Column
    private UUID updatedBy;

    @Column
    private java.time.LocalDate deletedAt;

    public enum PostStatus { published, deleted, moderated }
}
