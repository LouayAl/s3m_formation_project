package com.s3m.formation.api.kpi.client.dto;

import java.math.BigDecimal;

public record ClientHoursByFamilleFormationKpiDto(
        String familleFormation,
        BigDecimal totalHeures
) {
}
