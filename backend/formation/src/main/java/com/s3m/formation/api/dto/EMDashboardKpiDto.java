package com.s3m.formation.api.dto;

public record EMDashboardKpiDto(
        long sessionsEnCours,
        long sessionsTerminees,
        long sessionsPlanifiees,
        long totalParticipants,       // distinct employees enrolled across all sessions
        long participantsEnDirect,    // enrolled in EN_COURS sessions
        long totalEmployes,
        long evaluationsSaisies
) {}