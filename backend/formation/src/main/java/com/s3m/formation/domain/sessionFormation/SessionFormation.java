package com.s3m.formation.domain.sessionFormation;

import com.s3m.formation.api.exception.SessionFormationException;
import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.formateur.Formateur;
import com.s3m.formation.domain.formation.Formation;
import com.s3m.formation.domain.participation.Participation;
import com.s3m.formation.domain.reservation.DemandeReservation;
import jakarta.persistence.*;
import lombok.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "session_formation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionFormation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_session")
    private Integer idSession;

    @Column(name = "reference_session", unique = true)
    private String referenceSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formation", nullable = false)
    private Formation formation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formateur")
    private Formateur formateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entreprise")
    private Entreprise entreprise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fournisseur")
    private Entreprise fournisseur;

    @OneToMany(mappedBy = "session")
    private List<Participation> participations;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private SessionFormationStatut statut;

    @Column(name = "d_jours")
    private BigDecimal dJours;

    @Column(name = "d_heures")
    private BigDecimal dHeures;


    @PrePersist
    public void prePersist() {
        if (this.statut == null) {
            this.statut = SessionFormationStatut.PLANIFIEE;
        }
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_demande",
            nullable = false,
            unique = true
    )
    private DemandeReservation demande; // let's ignore this for now we don't want to work on reservations yet

    public void demarrer(LocalDate today) {

        if (this.statut != SessionFormationStatut.PLANIFIEE) {
            throw new SessionFormationException(
                    "La session doit être PLANIFIEE pour démarrer"
            );
        }

        if (this.formateur == null) {
            throw new SessionFormationException(
                    "Un formateur doit être assigné avant le démarrage"
            );
        }

        if (this.fournisseur == null) {
            throw new SessionFormationException(
                    "Un fournisseur doit être assigné avant le démarrage"
            );
        }

        if (this.dateDebut.isAfter(today)) {
            throw new SessionFormationException(
                    "La session ne peut pas démarrer dans le futur"
            );
        }

        this.statut = SessionFormationStatut.EN_COURS;
    }

    public void terminer() {
        if (this.statut != SessionFormationStatut.EN_COURS) {
            throw new IllegalStateException(
                    "La session doit être EN_COURS pour être terminée"
            );
        }
        this.statut = SessionFormationStatut.TERMINEE;
    }

    public void annuler() {
        if (this.statut == SessionFormationStatut.TERMINEE) {
            throw new IllegalStateException(
                    "Une session terminée ne peut pas être annulée"
            );
        }
        this.statut = SessionFormationStatut.ANNULEE;
    }

}
