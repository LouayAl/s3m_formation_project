package com.s3m.formation.domain.sessionFormation.admin.dto;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
public class SessionFormationAdminUpdateRequest {

    private Integer formateurId;     // optional
    private Integer fournisseurId;   // optional

    private LocalDate dateDebut;     // optional
    private LocalDate dateFin;       // optional
}
