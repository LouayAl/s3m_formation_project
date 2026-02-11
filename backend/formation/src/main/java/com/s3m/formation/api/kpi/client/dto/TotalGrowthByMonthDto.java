package com.s3m.formation.api.kpi.client.dto;

import java.math.BigDecimal;

public record TotalGrowthByMonthDto(
        String mois,
        String formation,
        BigDecimal totalHeures
) {
}
