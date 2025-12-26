package com.s3m.formation.domain.sessionFormation.sessionFormationAudit;

import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "session_formation_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionFormationAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_audit")
    private Long idAudit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_session")
    private SessionFormation session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionFormationStatut statutAvant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionFormationStatut statutApres;

    @Column(nullable = false)
    private String modifiePar; // email or username

    @Column(nullable = false)
    private LocalDateTime dateModification;

    @Column
    private String commentaire;
}
