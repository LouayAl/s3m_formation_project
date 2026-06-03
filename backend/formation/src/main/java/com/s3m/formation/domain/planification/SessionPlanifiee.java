package com.s3m.formation.domain.planification;

import com.s3m.formation.domain.entreprise.Entreprise;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "session_planifiee",
        indexes = @Index(columnList = "entreprise_id, date_session")
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionPlanifiee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @Column(name = "date_session", nullable = false)
    private LocalDate dateSession;

    @Column(name = "d_heures", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal dHeures = BigDecimal.valueOf(8);

    @Column(name = "nb_participants")
    @Builder.Default
    private Integer nbParticipants = 0;

    @Column(name = "notes", length = 255)
    private String notes;
}