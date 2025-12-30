package com.s3m.formation.domain.EvaluationAFroid;

import com.s3m.formation.domain.participation.Participation;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "evaluation_a_froid")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationAFroid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_eval_froid")
    private Integer idEvalFroid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_participation")
    private Participation participation;

    private String evaluationParticipant;

    @Column(name = "evaluation_n_plus_1")
    private String evaluationNPlus1;

    @Column(name = "date_evaluation_a_froid")
    private LocalDate dateEvaluationAFroid;
    private BigDecimal tauxEfficacite;
}
