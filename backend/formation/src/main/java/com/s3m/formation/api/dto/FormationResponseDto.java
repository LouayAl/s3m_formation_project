package com.s3m.formation.api.dto;

public record FormationResponseDto(
        Integer id,
        String module,
        String typeFormation,
        String familleFormation,
        String sousFamille,
        String referenceFormation,
        Integer annee
) {
}
