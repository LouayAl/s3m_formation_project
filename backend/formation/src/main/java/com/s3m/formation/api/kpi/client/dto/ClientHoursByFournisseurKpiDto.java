package com.s3m.formation.api.kpi.client.dto;

import java.math.BigDecimal;

public record ClientHoursByFournisseurKpiDto(
        String fournisseur,
        BigDecimal totalHeures
) {
}
