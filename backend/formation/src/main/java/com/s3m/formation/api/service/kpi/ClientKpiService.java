package com.s3m.formation.api.service.kpi;

import com.s3m.formation.api.kpi.client.dto.ClientKpiResponse;
import com.s3m.formation.api.kpi.client.dto.TotalGrowthByMonthDto;
import com.s3m.formation.api.kpi.client.dto.TotalGrowthKpiDto;

import java.util.List;

public interface ClientKpiService {
    ClientKpiResponse getClientKpis(Integer clientId);
    TotalGrowthKpiDto getTotalGrowthKpi(Integer entrepriseId, String period, String month);
}
