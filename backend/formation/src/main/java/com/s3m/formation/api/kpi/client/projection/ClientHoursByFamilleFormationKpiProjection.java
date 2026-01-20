package com.s3m.formation.api.kpi.client.projection;

import java.math.BigDecimal;

public interface ClientHoursByFamilleFormationKpiProjection {
    String getFamilleFormation();
    BigDecimal getTotalHeures();
}
