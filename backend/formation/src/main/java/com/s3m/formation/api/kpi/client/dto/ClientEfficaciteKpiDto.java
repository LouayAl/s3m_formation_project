package com.s3m.formation.api.kpi.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClientEfficaciteKpiDto(
        BigDecimal pourcentageEvalue,
        BigDecimal tauxEfficaciteMoyen,
        LocalDate dateDerniereEvaluation
) {
}
