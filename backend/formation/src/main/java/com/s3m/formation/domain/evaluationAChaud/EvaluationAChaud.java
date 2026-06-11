package com.s3m.formation.domain.evaluationAChaud;

import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "evaluation_a_chaud",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"id_session", "id_employe", "jour_evaluation"}
        )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EvaluationAChaud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_eval_chaud")
    private Integer idEvalChaud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_session", nullable = false)
    private SessionFormation session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employe", nullable = false)
    private Employe employe;

    @Column(name = "jour_evaluation")
    private LocalDate jourEvaluation;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "soumis_le")
    private LocalDateTime soumisLe;

    @OneToMany(mappedBy = "evalChaud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationReponse> reponses;
}