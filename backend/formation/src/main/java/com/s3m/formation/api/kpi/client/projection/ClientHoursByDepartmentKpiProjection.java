package com.s3m.formation.api.kpi.client.projection;

import java.math.BigDecimal;

public interface ClientHoursByDepartmentKpiProjection {
    String getDepartement();
    BigDecimal getTotalHeures();

}
