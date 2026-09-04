package com.s3m.formation.api.kpi.client.dto;

import java.math.BigDecimal;

public record SessionStatusKpiDto(
        BigDecimal totalHeures,
        Long totalSessions,
        Long totalParticipants
) {}