package com.s3m.formation.domain.reservation;


import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
public class DemandeReservationRequest {

    @NotNull
    private Integer idFormation;

    private Integer idFiche; // optional

    @NotNull
    private LocalDate dateDebutSouhaitee;

    @NotNull
    private LocalDate dateFinSouhaitee;

    private String commentaireClient;
}
