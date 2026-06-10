package com.s3m.formation.domain.coutFormation;

import com.s3m.formation.domain.sessionFormation.SessionFormation;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cout_formation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoutFormation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cout")
    private Integer idCout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_session")
    private SessionFormation session;

    @Column(name = "prix_heure_mad")
    private BigDecimal prixHeureMad;

    @Column(name = "prix_jour_mad")
    private BigDecimal prixJourMad;

    @Column(name = "autres_depenses")
    private BigDecimal autresDepenses;

    @Column(name = "cout_total")
    private BigDecimal coutTotal;

    @Column(name = "remboursement")
    private String remboursement;
}
