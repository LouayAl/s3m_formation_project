package com.s3m.formation.api.dto;

public record EmployeHeuresDto(
        Long idEmploye,
        String nom,
        String prenom,
        Double heures
) {}
