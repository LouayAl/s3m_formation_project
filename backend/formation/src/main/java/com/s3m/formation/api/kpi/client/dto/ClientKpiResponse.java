package com.s3m.formation.api.kpi.client.dto;

public record ClientKpiResponse(
        ClientIdentiteKpiDto identite,
        ClientVolumeKpiDto volume,
        ClientFinancierKpiDto financier,
        ClientFormationKpiDto formations,
        ClientPopulationKpiDto population,
        ClientEfficaciteKpiDto efficacite // nullable
) {
}
