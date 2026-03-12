package com.s3m.formation.api.service.kpi;

import com.s3m.formation.api.kpi.client.dto.ClientKpiResponse;
import com.s3m.formation.api.kpi.client.dto.TotalGrowthKpiDto;

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
}