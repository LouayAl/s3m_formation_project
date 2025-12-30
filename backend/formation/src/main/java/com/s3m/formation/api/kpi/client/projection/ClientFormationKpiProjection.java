package com.s3m.formation.api.kpi.client.projection;

import java.math.BigDecimal;

public interface ClientFormationKpiProjection {

    Long getTotalFormations();          // distinct modules
    String getFormationLaPlusSuivie();
    String getFamillePrincipale();
    BigDecimal getPourcentageInterne();
    BigDecimal getPourcentageExterne();
}
