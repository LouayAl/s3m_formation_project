package com.s3m.formation.api.kpi.client.dto;

public record EmployeGenderByDepartmentKpiDto(
        String departement,
        String genre,
        Long nombre
) {
}
