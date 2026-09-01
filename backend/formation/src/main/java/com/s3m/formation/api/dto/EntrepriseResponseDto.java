package com.s3m.formation.api.dto;

import com.s3m.formation.domain.entreprise.TypeEntreprise;

public record EntrepriseResponseDto(
        Integer idEntreprise,
        String nomEntreprise,
        TypeEntreprise typeEntreprise
) {
}
