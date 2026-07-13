package com.s3m.formation.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BesoinFormationResponseDto(
        Integer id,
        Integer entrepriseId,
        String entrepriseNom,
        String dept,
        String intitule,
        String populationCible,
        Integer nbCadre,
        Integer nbTam,
        Integer nbPro,
        Integer priorite,
        String periode,
        String objectifs,
        String competencesCiblees,
        String indicateursSucces,
        String evaluation,
        BigDecimal budgetEstimatif,
        String remarques,
        LocalDateTime dateCreation,
        LocalDateTime dateModification
) {
}