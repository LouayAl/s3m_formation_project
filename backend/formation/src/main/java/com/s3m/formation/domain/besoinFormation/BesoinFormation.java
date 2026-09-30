package com.s3m.formation.domain.besoinFormation;

import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.departement.Departement;
import com.s3m.formation.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "besoin_formation")
public class BesoinFormation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_besoin")
    private Integer idBesoin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entreprise", nullable = false)
    private Entreprise entreprise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departement")
    private Departement departement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_user_id")
    private User requestedBy;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "dept", length = 100)
    private String dept;

    // "Besoin en Formation" in the source sheet — the training need's title/description
    @Column(name = "intitule", nullable = false, length = 500)
    private String intitule;

    @Column(name = "population_cible", length = 150)
    private String populationCible;

    @Column(name = "nb_cadre")
    private Integer nbCadre;

    @Column(name = "nb_tam")
    private Integer nbTam;

    @Column(name = "nb_pro")
    private Integer nbPro;

    @Column(name = "priorite")
    private Integer priorite;

    @Column(name = "periode", length = 100)
    private String periode;

    @Column(name = "objectifs", columnDefinition = "TEXT")
    private String objectifs;

    @Column(name = "competences_ciblees", columnDefinition = "TEXT")
    private String competencesCiblees;

    @Column(name = "indicateurs_succes", columnDefinition = "TEXT")
    private String indicateursSucces;

    @Column(name = "evaluation", columnDefinition = "TEXT")
    private String evaluation;

    @Column(name = "budget_estimatif")
    private BigDecimal budgetEstimatif;

    @Column(name = "remarques", columnDefinition = "TEXT")
    private String remarques;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @PrePersist
    void onCreate() {
        if (this.status == null) this.status = "PENDING";
        this.dateCreation = LocalDateTime.now();
        this.dateModification = this.dateCreation;
    }

    @PreUpdate
    void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }
}
