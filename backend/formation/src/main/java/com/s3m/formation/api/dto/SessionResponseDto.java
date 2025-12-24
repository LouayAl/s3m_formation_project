package com.s3m.formation.api.dto;

import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;

import java.time.LocalDate;

public record SessionResponseDto(
        Integer idSession,
        LocalDate dateDebut,
        LocalDate dateFin,
        SessionFormationStatut statut,
        String formateurNom,
        String fournisseurNom
) {
}
