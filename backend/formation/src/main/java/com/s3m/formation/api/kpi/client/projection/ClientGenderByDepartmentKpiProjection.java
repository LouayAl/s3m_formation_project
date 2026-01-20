package com.s3m.formation.api.kpi.client.projection;

public interface GenderByDepartmentKpiProjection {
    String getDepartement();
    String getGenre(); // H or F
    Long getNombre();
}
