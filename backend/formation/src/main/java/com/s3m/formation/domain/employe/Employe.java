package com.s3m.formation.domain.employe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.s3m.formation.domain.departement.Departement;
import com.s3m.formation.domain.entreprise.Entreprise;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Entity
@Table(name = "employe", indexes = {
    @Index(name = "idx_employe_id_entreprise", columnList = "id_entreprise"),
    @Index(name = "idx_employe_nom", columnList = "nom"),
    @Index(name = "idx_employe_prenom", columnList = "prenom"),
    @Index(name = "idx_employe_matricule", columnList = "matricule"),
    @Index(name = "idx_employe_cin", columnList = "cin"),
    @Index(name = "idx_employe_entreprise_nom", columnList = "id_entreprise,nom")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_employe")
    private Integer idEmploye;

    private String nom;
    private String prenom;
    private String telephone;

    @Column(unique = true)
    private String email;
    private String cnss;
    private String cin;

    @Column(unique = true)
    private String matricule;
    private String csp;
    private String fonction;
    private String typeContrat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departement")
    private Departement departement;

    private Character f_h; // gender
    private LocalDate dateEmbauche;
    private LocalDate dateNaissance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entreprise")
    private Entreprise entreprise;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_manager")
//    @JsonIgnore
//    private Employe manager;
}
