package com.s3m.formation.api.dto;

import com.s3m.formation.domain.departement.Departement;
import com.s3m.formation.domain.entreprise.Entreprise;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class EmployeResponseDto {
    private Integer idEmploye;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String cin;
    private String cnss;
    private String matricule;
    private String csp;
    private String fonction;
    private String typeContrat;
    private Character f_h;
    private LocalDate dateEmbauche;
    private LocalDate dateNaissance;

    private Integer entrepriseId;
    private String entrepriseNom;

    private Integer departementId;
    private String departementNom;
}
