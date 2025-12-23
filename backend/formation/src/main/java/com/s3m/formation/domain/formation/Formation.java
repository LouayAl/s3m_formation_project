package com.s3m.formation.domain.formation;

import com.s3m.formation.domain.fiche.FicheTechniqueFormation;
import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "formation")
public class Formation {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formation")
    private Integer idFormation;

    @Getter
    @Column(name = "module", nullable = false, length = 255)
    private String module;

    @Setter
    @Getter
    @Column(name = "type_formation", nullable = false, length = 150)
    private String typeFormation;

    @Setter
    @Getter
    @Column(name = "famille_formation", nullable = false, length = 150)
    private String familleFormation;

    @Getter
    @Column(name = "sous_famille", length = 150)
    private String sousFamille;

    @Column(name = "interne_externe", length = 50)
    private String interneExterne;

    @Getter
    @Column(name = "annee")
    private Integer annee;

    @Getter
    @Column(name = "reference_formation", nullable = false, length = 50, unique = true)
    private String referenceFormation;

    // --- Constructors ---

    public Formation() {
    }

    @JsonIgnore
    @OneToMany(
            mappedBy = "formation",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<FicheTechniqueFormation> fichesTechniques;

}
