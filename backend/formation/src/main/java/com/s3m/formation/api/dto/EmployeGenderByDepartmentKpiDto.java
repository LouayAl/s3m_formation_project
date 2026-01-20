package com.s3m.formation.api.dto;

import java.util.List;

public record EmployeGenderByDepartmentKpiDto(
        String departement,
        String genre,
        Double totalHeures,
        List<EmployeHeuresDto> employes
) {
}

