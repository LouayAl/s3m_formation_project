package com.s3m.formation.api.kpi.client.projection;

public interface GenderHoursKpiProjection {
    String getLabel();       // H or F
    Double getTotalHeures(); // SUM of training hours
    Long getNombreEmployes();   // COUNT(DISTINCT employe)
}
