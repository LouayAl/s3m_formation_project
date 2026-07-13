package com.s3m.formation.domain.quiz;

import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(
        name = "quiz_reponse",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_session", "id_employe"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizReponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reponse")
    private Integer idReponse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_session", nullable = false)
    private SessionFormation session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employe", nullable = false)
    private Employe employe;

    // { "1": "FAUX", "2": "b", ... }
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reponses", columnDefinition = "jsonb", nullable = false)
    private Map<String, String> reponses;

    @Column(name = "score")
    private Integer score;

    @Column(name = "soumis_le")
    private LocalDateTime soumisLe;

    @Column(name = "debut_le")
    private LocalDateTime debutLe;
}