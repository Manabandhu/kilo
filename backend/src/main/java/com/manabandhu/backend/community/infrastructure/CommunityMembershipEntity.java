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
@Table(name = "community_memberships")
@Getter
@Setter
@NoArgsConstructor
public class CommunityMembershipEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID communityId;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role = MemberRole.member;

    public enum MemberRole { member, moderator }
}
