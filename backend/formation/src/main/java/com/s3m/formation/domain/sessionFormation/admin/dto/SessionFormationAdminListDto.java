package com.s3m.formation.domain.sessionFormation.admin.dto;


import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SessionFormationAdminListDto {
    private Integer sessionId;

    private String formationModule;

    private String entrepriseNom;

    private SessionFormationStatut statut;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private String formateurNom;     // nullable
    private String fournisseurNom;   // nullable
}
