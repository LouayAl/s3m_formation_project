package com.s3m.formation.api.kpi.client.dto;

public record ClientParticipantsByDepartmentKpiDto(
        Integer departementId,
        String departement,
        Long nbParticipants
) {}