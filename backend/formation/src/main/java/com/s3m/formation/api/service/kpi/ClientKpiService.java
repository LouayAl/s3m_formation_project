package com.s3m.formation.api.service.kpi;

import com.s3m.formation.api.kpi.client.dto.ClientKpiResponse;
import com.s3m.formation.api.kpi.client.dto.TotalGrowthKpiDto;
import com.s3m.formation.api.kpi.client.dto.VisibiliteKpiDto;
import com.s3m.formation.api.kpi.client.dto.VisibiliteSessionDto;

import java.time.LocalDate;
import java.util.List;

public interface ClientKpiService {

    ClientKpiResponse getClientKpis(Integer clientId, Integer[] years);

    TotalGrowthKpiDto getTotalGrowthKpi(
            Integer entrepriseId,
            String period,
            String month,
            Integer[] years   // ← added
    );

    List<Integer> getAvailableYears(Integer clientId);

    VisibiliteKpiDto getVisibiliteKpis(Integer clientId, LocalDate start, LocalDate end);

    List<VisibiliteSessionDto> getVisibiliteSessions(Integer clientId, LocalDate start, LocalDate end);
}