package com.s3m.formation.api.dto;

public record ParticipantResponseDto(
        Integer idEmploye,
        String nom,
        String prenom,
        String email,
        String telephone
) {
}
