package com.s3m.formation.api.dto;

import java.math.BigDecimal;

public record FormationResponseDto(
        Integer id,
        String module,
        String typeFormation,
        String familleFormation,
        String sousFamille,
        String referenceFormation,
        Integer annee,
        BigDecimal dureeHeures,
        BigDecimal dureeJours,
        BigDecimal prixHeureMad,
        BigDecimal prixJourMad
) {
}
