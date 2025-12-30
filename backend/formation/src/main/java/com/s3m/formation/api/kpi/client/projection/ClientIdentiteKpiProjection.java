package com.s3m.formation.api.kpi.client.projection;

import java.time.LocalDate;

public interface ClientIdentiteKpiProjection {
    String getNomClient();
    Integer getPremiereAnnee();
    Integer getDerniereAnnee();
    LocalDate getDatePremiere();
    LocalDate getDateDerniere();
}
