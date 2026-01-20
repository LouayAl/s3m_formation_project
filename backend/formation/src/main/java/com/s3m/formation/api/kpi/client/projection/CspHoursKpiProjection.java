package com.s3m.formation.api.kpi.client.projection;

public interface CspHoursKpiProjection {
    String getCsp();        // A, E, P, O, etc.
    Double getTotalHeures(); // sum of session hours
    Long getNombreEmployes(); // number of distinct participants
}