package com.s3m.formation.domain.formation;

import com.s3m.formation.domain.fiche.FicheTechniqueFormation;
import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    // --- Constructors ---

    public Formation() {
    }

    // --- Getters & Setters ---

    public Integer getIdFormation() {
        return idFormation;
    }

    public void setIdFormation(Integer idFormation) {
        this.idFormation = idFormation;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getTypeFormation() {
        return typeFormation;
    }

    public void setTypeFormation(String typeFormation) {
        this.typeFormation = typeFormation;
    }

    public String getFamilleFormation() {
        return familleFormation;
    }

    public void setFamilleFormation(String familleFormation) {
        this.familleFormation = familleFormation;
    }

    public String getSousFamille() {
        return sousFamille;
    }

    public void setSousFamille(String sousFamille) {
        this.sousFamille = sousFamille;
    }

    public String getInterneExterne() {
        return interneExterne;
    }

    public void setInterneExterne(String interneExterne) {
        this.interneExterne = interneExterne;
    }

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public String getReferenceFormation() {
        return referenceFormation;
    }

    public void setReferenceFormation(String referenceFormation) {
        this.referenceFormation = referenceFormation;
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
