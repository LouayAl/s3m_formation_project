package com.s3m.formation.api.dto;

import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SessionFormationResponseDto(
        Integer idSession,
        String referenceSession,
        Integer formationId,
        String formation,
        String entrepriseNom,
        String fournisseurNom,
        String formateurNomComplet,
        LocalDate dateDebut,
        LocalDate dateFin,
        BigDecimal dHeures,
        BigDecimal dJours,
        SessionFormationStatut statut,
        int participantsCount,
        List<ParticipantResponseDto> participants
        ) {
}
