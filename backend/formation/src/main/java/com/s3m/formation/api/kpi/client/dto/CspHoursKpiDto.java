package com.s3m.formation.api.kpi.client.dto;

public record CspHoursKpiDto(
        String csp,
        Double totalHeures,
        Long nombreEmployes
) {
}
