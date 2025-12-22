package com.s3m.formation.api.dto;

import java.time.LocalDate;

public record SessionResponseDto(
        Integer idSession,
        LocalDate dateDebut,
        LocalDate dateFin,
        String statut,
        String formateurNom,
        String fournisseurNom
) {
}
