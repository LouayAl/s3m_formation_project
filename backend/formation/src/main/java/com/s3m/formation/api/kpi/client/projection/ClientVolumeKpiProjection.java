package com.s3m.formation.api.kpi.client.projection;

import java.math.BigDecimal;

public interface ClientVolumeKpiProjection {
    Long getTotalSessions();
    Long getTotalParticipants();
    BigDecimal getTotalJours();
    BigDecimal getTotalHeures();
}
