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
        Integer idEntreprise,       // ✅ add
        String entrepriseNom,
        Integer idFournisseur,      // ✅ add
        String fournisseurNom,
        Integer idFormateur,        // ✅ add
        String formateurNomComplet,
        LocalDate dateDebut,
        LocalDate dateFin,
        BigDecimal dHeures,
        BigDecimal dJours,
        SessionFormationStatut statut,
        int participantsCount,
        List<ParticipantResponseDto> participants,
        String lieu,
        Boolean facture              // ✅ new — only populated for ADMIN_FINANCE, null otherwise
) {
}