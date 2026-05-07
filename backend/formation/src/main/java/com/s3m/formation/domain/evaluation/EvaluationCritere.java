package com.s3m.formation.domain.evaluation;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "evaluation_critere",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"id_evaluation", "critere_index"}
        )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EvaluationCritere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evaluation", nullable = false)
    private Evaluation evaluation;

    @Column(name = "critere_index", nullable = false)
    private Integer critereIndex;

    @Column(nullable = false)
    private Integer score; // 1–4
}