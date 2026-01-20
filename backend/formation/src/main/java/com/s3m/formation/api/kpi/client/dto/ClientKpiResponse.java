package com.s3m.formation.api.kpi.client.dto;

import java.util.List;

public record ClientKpiResponse(
        ClientFinancierKpiDto financier, // used
        ClientPopulationKpiDto population, // used
        List<ClientParticipantsByDepartmentKpiDto> participantsByDepartment, // used
        List<ClientHoursByDepartmentKpiDto> hoursByDepartment, // used
        List<ClientHoursByFournisseurKpiDto> hoursByFournisseur, // used
        List<ClientHoursByFamilleFormationKpiDto> hoursByFamilleFormation, // used
        List<ClientFinancierByRemboursementDto> remboursementByType, // NEW field for pie chart // used
        Double totalFormationHours,
        Long totalSessions
        ) {
}
