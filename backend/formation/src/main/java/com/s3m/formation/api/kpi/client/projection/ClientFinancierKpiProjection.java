package com.s3m.formation.api.kpi.client.projection;

import java.math.BigDecimal;

public interface ClientFinancierKpiProjection {
    BigDecimal getCoutTotal();
    BigDecimal getCoutRembourse();
    BigDecimal getCoutNonRembourse();
    BigDecimal getTotalJours();
    Long getTotalParticipants();
}
