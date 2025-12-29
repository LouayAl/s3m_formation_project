package com.s3m.formation.api.service.kpi;

import com.s3m.formation.api.kpi.client.dto.ClientKpiResponse;

public interface ClientKpiService {
    ClientKpiResponse getClientKpis(Integer clientId);
}
