package com.s3m.formation.auth.dto;

public record LoginResponse(
        String token,
        String email,
        String role,
        String prenom,
        String nom,
        Integer entrepriseId
) {}

