package com.s3m.formation.domain.evaluationAChaud;

import com.s3m.formation.domain.participation.Participation;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "evaluation_a_chaud")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationAChaud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_eval_chaud")
    private Integer idEvalChaud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_participation")
    private Participation participation;

    @Column(name = "evaluation_a_chaud")
    private String evaluation;
}
