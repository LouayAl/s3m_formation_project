package com.s3m.formation.api.kpi.client.projection;

import java.math.BigDecimal;

public interface ClientHoursByFournisseurKpiProjection {
    String getFournisseur();
    BigDecimal getTotalHeures();
}
