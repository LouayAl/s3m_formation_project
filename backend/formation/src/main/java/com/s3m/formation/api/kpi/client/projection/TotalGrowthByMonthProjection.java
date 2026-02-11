package com.s3m.formation.api.kpi.client.projection;

import java.math.BigDecimal;

public interface TotalGrowthByMonthProjection {
    String getMois();        // e.g., "Jan 2025"
    String getFormation();   // e.g., "Communication et bus..."
    BigDecimal getTotalHeures();
}
