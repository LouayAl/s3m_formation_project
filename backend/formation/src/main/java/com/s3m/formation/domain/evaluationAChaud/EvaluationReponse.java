package com.s3m.formation.domain.evaluationAChaud;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "evaluation_reponse",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_eval_chaud", "id_question"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EvaluationReponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reponse")
    private Integer idReponse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_eval_chaud", nullable = false)
    private EvaluationAChaud evalChaud;

    @Column(name = "id_question", nullable = false)
    private Integer idQuestion; // 1-13

    @Column(name = "score", nullable = false)
    private Integer score; // 1-4
}