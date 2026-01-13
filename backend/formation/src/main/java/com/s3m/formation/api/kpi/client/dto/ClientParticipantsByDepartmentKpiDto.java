package com.s3m.formation.api.kpi.client.dto;

public record ClientParticipantsByDepartmentKpiDto(

        String departement,
        Long nbParicipants
) {}
