package com.s3m.formation.api.kpi.client.dto;

import java.math.BigDecimal;

public record ClientHoursByDepartmentKpiDto(
        Integer departementId,
        String departement,
        BigDecimal totalHeures
) {}