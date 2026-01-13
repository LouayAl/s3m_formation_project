package com.s3m.formation.api.kpi.client.dto;

import java.math.BigDecimal;

public record ClientHoursByDepartmentKpiDto(
        String departement,
        BigDecimal totalHeures
) {
}
