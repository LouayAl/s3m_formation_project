package com.s3m.formation.domain.fiche;


import com.s3m.formation.domain.formation.Formation;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "fiche_technique_formation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_fiche_formation_version",
                        columnNames = {"id_formation", "version_numero"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FicheTechniqueFormation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fiche")
    private Integer idFiche;

    /* =========================
       RELATION
       ========================= */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_formation", nullable = false)
    private Formation formation;

    /* =========================
       VERSIONING
       ========================= */

    @Column(name = "version_numero", nullable = false)
    private Integer versionNumero;

    @Column(name = "statut", nullable = false)
    private String statut; // ACTIVE / ARCHIVED / DRAFT

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_activation")
    private LocalDate dateActivation;

    @Column(name = "date_archivage")
    private LocalDate dateArchivage;

    /* =========================
       CONTENT
       ========================= */

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String objectifs;

    @Column(columnDefinition = "TEXT")
    private String competencesCible;

    @Column(columnDefinition = "TEXT")
    private String prerequis;

    @Column(columnDefinition = "TEXT")
    private String populationCible;

    @Column(columnDefinition = "TEXT")
    private String programme;

    /* =========================
       PEDAGOGICAL PARAMETERS
       ========================= */

    @Column(name = "duree_jours")
    private BigDecimal dureeJours;

    @Column(name = "duree_heures")
    private BigDecimal dureeHeures;

    @Column(name = "nb_participants_min")
    private Integer nbParticipantsMin;

    @Column(name = "nb_participants_max")
    private Integer nbParticipantsMax;
}
