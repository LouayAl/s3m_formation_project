package com.s3m.formation.domain.sessionFormation;

import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.formateur.Formateur;
import com.s3m.formation.domain.formation.Formation;
import com.s3m.formation.domain.reservation.DemandeReservation;
import jakarta.persistence.*;
import lombok.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formation", nullable = false)
    private Formation formation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_formateur")
    private Formateur formateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fournisseur")
    private Entreprise fournisseur;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "statut")
    private String statut;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_demande",
            nullable = false,
            unique = true
    )
    private DemandeReservation demande;
}
