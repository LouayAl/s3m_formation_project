package com.s3m.formation.api.dto;

import com.s3m.formation.domain.reservation.DemandeReservationStatut;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DemandeReservationResponse {

    private Integer idDemande;
    private DemandeReservationStatut statut;
    private LocalDate dateDebutSouhaitee;
    private LocalDate dateFinSouhaitee;
}