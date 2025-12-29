package com.s3m.formation.api.kpi.client.dto;

import java.math.BigDecimal;

public record ClientFormationKpiDto(
        Integer nombreFormationsDistinctes,
        String formationLaPlusSuivie,
        String familleFormationPrincipale,
        BigDecimal pourcentageInterne,
        BigDecimal pourcentageExterne
) {
}
