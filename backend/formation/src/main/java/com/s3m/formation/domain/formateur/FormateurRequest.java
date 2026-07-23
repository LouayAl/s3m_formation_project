package com.s3m.formation.domain.formateur;

public record FormateurRequest(
        String nom,
        String prenom,
        String email,
        String telephone,
        Boolean actif,
        Integer entrepriseId
) {}