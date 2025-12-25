package com.s3m.formation.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SessionFormationAdminUpdateRequest {
    private Integer formateurId;   // nullable
    private Integer fournisseurId; // nullable

    private LocalDate dateDebut;
    private LocalDate dateFin;
}
