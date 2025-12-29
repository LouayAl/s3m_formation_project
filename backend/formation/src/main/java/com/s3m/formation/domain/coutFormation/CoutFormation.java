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

    private BigDecimal prixHeureMad;
    private BigDecimal prixJourMad;
    private BigDecimal autresDepenses;
    private BigDecimal coutTotal;

    private String remboursement;
}
