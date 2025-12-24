package com.s3m.formation.api.dto;

import com.s3m.formation.domain.reservation.DemandeReservationStatut;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DemandeReservationAdminDto {
    private Integer idDemande;

    private String entrepriseNom;

    private String formationModule;

    private DemandeReservationStatut statut;

    private LocalDate dateDebutSouhaitee;
    private LocalDate dateFinSouhaitee;

    private LocalDateTime dateCreation;

    private Integer sessionId; // nullable
}
