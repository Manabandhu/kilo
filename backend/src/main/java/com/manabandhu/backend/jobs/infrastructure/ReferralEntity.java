package com.manabandhu.backend.jobs.infrastructure;

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
@Table(name = "referrals")
@Getter
@Setter
@NoArgsConstructor
public class ReferralEntity extends BaseEntity {

    private UUID jobId;

    @Column(nullable = false)
    private String kind;

    @Column(nullable = false)
    private UUID fromUserId;

    private UUID toUserId;

    private String company;

    @Column(length = 2000)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReferralStatus status = ReferralStatus.open;

    public enum ReferralStatus { open, connected, closed }
}
