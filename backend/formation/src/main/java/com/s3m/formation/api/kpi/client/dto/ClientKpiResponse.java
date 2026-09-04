package com.s3m.formation.api.kpi.client.dto;

import java.math.BigDecimal;
import java.util.List;

public record ClientKpiResponse(
        ClientFinancierKpiDto financier,
        ClientPopulationKpiDto population,
        List<ClientParticipantsByDepartmentKpiDto> participantsByDepartment,
        List<ClientHoursByDepartmentKpiDto> hoursByDepartment,
        List<ClientHoursByFournisseurKpiDto> hoursByFournisseur,
        List<ClientHoursByFamilleFormationKpiDto> hoursByFamilleFormation,
        List<ClientFinancierByRemboursementDto> remboursementByType,
        BigDecimal totalFormationHours,
        Long totalSessions,
        SessionStatusKpiDto realiseeKpi,
        SessionStatusKpiDto planifieeKpi,
        SessionStatusKpiDto autresKpi
) {}
