package com.s3m.formation.api.kpi.client.dto;

public record GenderHoursKpiDto(
        String genre,
        Double totalHeures,
        Long nombreEmployes
) {
}
