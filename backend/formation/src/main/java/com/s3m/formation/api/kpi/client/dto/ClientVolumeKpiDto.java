package com.s3m.formation.api.kpi.client.dto;

import java.math.BigDecimal;

public record ClientVolumeKpiDto(
        Integer totalSessions,
        Integer totalParticipants,
        BigDecimal totalJoursFormation,
        BigDecimal totalHeuresFormation
) {
}
