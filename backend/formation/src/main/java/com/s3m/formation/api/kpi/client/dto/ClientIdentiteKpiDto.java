package com.s3m.formation.api.kpi.client.dto;

import java.time.LocalDate;

public record ClientIdentiteKpiDto(
        Integer idClient,
        String nomClient,
        Integer premiereAnneeActivite,
        Integer derniereAnneeActivite,
        LocalDate datePremiereFormation,
        LocalDate dateDerniereFormation
) {
}
