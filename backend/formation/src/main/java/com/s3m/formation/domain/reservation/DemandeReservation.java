package com.s3m.formation.domain.reservation;

import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.fiche.FicheTechniqueFormation;
import com.s3m.formation.domain.formation.Formation;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(name = "demande_reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande")
    private Integer idDemande;

    /* =========================
       RELATIONS
       ========================= */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_entreprise", nullable = false)
    private Entreprise entreprise;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_formation", nullable = false)
    private Formation formation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_fiche", nullable = false)
    private FicheTechniqueFormation ficheTechnique;

    /* =========================
       REQUEST DETAILS
       ========================= */

    @Column(name = "date_debut_souhaitee")
    private LocalDate dateDebutSouhaitee;

    @Column(name = "date_fin_souhaitee")
    private LocalDate dateFinSouhaitee;

    @Column(name = "statut", nullable = false)
    private String statut; // REQUESTED, VALIDATED, REJECTED, ...

    @Column(name = "commentaire_client")
    private String commentaireClient;

    @Column(name = "commentaire_admin")
    private String commentaireAdmin;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
}
