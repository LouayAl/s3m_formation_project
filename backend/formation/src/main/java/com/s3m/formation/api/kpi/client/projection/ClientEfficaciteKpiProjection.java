package com.s3m.formation.api.kpi.client.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ClientEfficaciteKpiProjection {
    Long getTotalEvals();
    Long getEvalCount();  // with tauxEfficacite not null
    BigDecimal getSumTaux();
    LocalDate getLastEvalDate();
}
