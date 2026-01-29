package com.s3m.formation.api.dto;

import java.time.LocalDate;

public record EmployeCreateDto(
        String nom,
        String prenom,
        String email,
        String telephone,
        String cin,
        String cnss,
        String matricule,
        String csp,
        String fonction,
        String typeContrat,
        Character f_h,
        LocalDate dateEmbauche,
        LocalDate dateNaissance,
        Integer idEntreprise,
        Integer idDepartement
) {
}
