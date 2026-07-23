package com.s3m.formation.domain.formateur;

public record FormateurResponseDto(
        Integer idFormateur,
        String nom,
        String prenom,
        String email,
        String telephone,
        Boolean actif,
        Integer entrepriseId,
        String entrepriseNom
) {}