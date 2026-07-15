package com.manabandhu.backend.immigration.infrastructure;

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
@Table(name = "immigration_questions")
@Getter
@Setter
@NoArgsConstructor
public class ImmigrationQuestionEntity extends BaseEntity {

    private UUID resourceId;

    @Column(nullable = false, length = 4000)
    private String body;

    @Column(length = 6000)
    private String answer;

    private UUID answeredById;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus status = QuestionStatus.open;

    public enum QuestionStatus { open, answered, flagged }
}
