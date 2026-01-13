package com.s3m.formation.api.kpi.client.dto;

import java.util.List;

public record ClientKpiResponse(
        ClientIdentiteKpiDto identite,
        ClientVolumeKpiDto volume,
        ClientFinancierKpiDto financier,
        ClientFormationKpiDto formations,
        ClientPopulationKpiDto population,
        ClientEfficaciteKpiDto efficacite, // nullable
        List<ClientParticipantsByDepartmentKpiDto> participantsByDepartment,
        List<ClientHoursByDepartmentKpiDto> hoursByDepartment,
        List<ClientHoursByFournisseurKpiDto> hoursByFournisseur,
        List<ClientHoursByFamilleFormationKpiDto> hoursByFamilleFormation
) {
}
