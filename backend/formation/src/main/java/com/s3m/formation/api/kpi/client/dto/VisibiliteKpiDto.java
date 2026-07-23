package com.s3m.formation.api.kpi.client.dto;

public record VisibiliteKpiDto(
        long nbSessionsPlanifiees,
        double moyenneParticipantsParSession,
        long nbSessionsZeroParticipant
) {}