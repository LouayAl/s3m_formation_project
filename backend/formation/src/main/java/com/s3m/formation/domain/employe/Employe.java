package com.s3m.formation.domain.employe;

import com.s3m.formation.domain.entreprise.Entreprise;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employe")
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

    private String cin;
    private String matricule;
    private String csp;
    private String fonction;
    private String typeContrat;
    private Character f_h; // gender
    private LocalDate dateEmbauche;
    private LocalDate dateNaissance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entreprise")
    private Entreprise entreprise;
}
