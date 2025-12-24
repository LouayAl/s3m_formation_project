package com.s3m.formation.domain.reservation;

import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.fiche.FicheTechniqueFormation;
import com.s3m.formation.domain.formation.Formation;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private DemandeReservationStatut  statut; // EN_ATTENTE, VALIDEE, REFUSEE, ANNULEE
    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        if (this.statut == null) {
            this.statut = DemandeReservationStatut.EN_ATTENTE;
        }
    }

    @Column(name = "commentaire_client")
    private String commentaireClient;

    @Column(name = "commentaire_admin")
    private String commentaireAdmin;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @OneToOne(mappedBy = "demande", fetch = FetchType.LAZY)
    private SessionFormation sessionFormation;

}
