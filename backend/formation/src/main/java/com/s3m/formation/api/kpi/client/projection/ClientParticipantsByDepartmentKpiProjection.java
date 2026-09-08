package com.s3m.formation.api.kpi.client.projection;

public interface ClientParticipantsByDepartmentKpiProjection {
    Integer getDepartementId();
    String getDepartement();
    Long getNbParticipants();
}