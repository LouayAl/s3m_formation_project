package com.s3m.formation.api.kpi.client.dto;

import java.util.List;

public record ClientPopulationKpiDto(
        List<RepartitionKpiItemDto> repartitionCsp,
        List<RepartitionKpiItemDto> repartitionFonction,
        List<RepartitionKpiItemDto> repartitionTypeContrat,
        List<RepartitionKpiItemDto> repartitionGenre,
        List<EmployeGenderByDepartmentKpiDto> genderByDepartment,
        List<GenderHoursKpiDto> genderHours,
        List<CspHoursKpiDto> cspHours,
        Long totalParticipants
) {
}
