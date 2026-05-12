package com.s3m.formation.domain.evaluation;

import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "evaluation",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"id_session", "id_employe", "jour"}
        )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_session", nullable = false)
    private SessionFormation session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_employe", nullable = false)
    private Employe employe;

    @Column(nullable = false)
    private Integer jour;

    // PRESENT | ABSENT | RETARD
    @Column(nullable = false, length = 10)
    private String presence = "PRESENT";

    @Column(columnDefinition = "TEXT")
    private String remarques;

    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EvaluationCritere> criteres = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "duree_heures")
    private BigDecimal dureeHeures;
}