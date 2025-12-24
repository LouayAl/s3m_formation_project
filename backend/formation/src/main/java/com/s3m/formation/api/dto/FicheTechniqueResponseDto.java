package com.s3m.formation.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;


public record FicheTechniqueResponseDto(
        Integer idFiche,
        Integer versionNumero,
        com.s3m.formation.domain.fiche.FicheStatut statut,
        LocalDateTime dateCreation,
        LocalDate dateActivation,
        String description,
        String objectifs,
        String competencesCible,
        String prerequis,
        String populationCible,
        String programme,
        java.math.BigDecimal dureeJours,
        Integer nbParticipantsMin,
        Integer nbParticipantsMax
) {
}
