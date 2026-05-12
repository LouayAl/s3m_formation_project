package com.s3m.formation.domain.formation;

import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.fiche.FicheTechniqueFormation;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "formation")
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formation")
    private Integer idFormation;

    @Column(name = "module", nullable = false, length = 255)
    private String module;

    @Column(name = "type_formation", nullable = false, length = 150)
    private String typeFormation;

    @Column(name = "famille_formation", nullable = false, length = 150)
    private String familleFormation;

    @Column(name = "sous_famille", length = 150)
    private String sousFamille;

    @Column(name = "interne_externe", length = 50)
    private String interneExterne;

    @Column(name = "annee")
    private Integer annee;

    @Column(name = "reference_formation", nullable = false, length = 50, unique = true)
    private String referenceFormation;

    @Column(name = "d_heures")
    private BigDecimal dureeHeures;

    @Column(name = "d_jours")
    private BigDecimal dureeJours;

    @Column(name = "prix_heure_mad")
    private BigDecimal prixHeureMad;

    @Column(name = "prix_jour_mad")
    private BigDecimal prixJourMad;

    @JsonIgnore
    @OneToMany(
            mappedBy = "formation",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<FicheTechniqueFormation> fichesTechniques;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entreprise")
    private Entreprise entreprise;

}
