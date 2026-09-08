package com.s3m.formation.api.service.kpi;

import com.s3m.formation.api.kpi.client.dto.ClientKpiResponse;
import com.s3m.formation.api.kpi.client.dto.TotalGrowthKpiDto;
import com.s3m.formation.api.kpi.client.dto.VisibiliteKpiDto;
import com.s3m.formation.api.kpi.client.dto.VisibiliteSessionDto;

import java.time.LocalDate;
import java.util.List;

public interface ClientKpiService {

    ClientKpiResponse getClientKpis(Integer clientId, Integer departementId, Integer[] years);

    List<Integer> getAvailableYears(Integer clientId, Integer departementId);

    TotalGrowthKpiDto getTotalGrowthKpi(Integer entrepriseId, Integer departementId, String period, String month, Integer[] years);

    VisibiliteKpiDto getVisibiliteKpis(Integer clientId, Integer departementId, LocalDate start, LocalDate end);

    List<VisibiliteSessionDto> getVisibiliteSessions(Integer clientId, Integer departementId, LocalDate start, LocalDate end);

    List<VisibiliteSessionDto> getPlanifiedSessionsForCalendar(Integer clientId, Integer departementId, LocalDate start, LocalDate end);
}