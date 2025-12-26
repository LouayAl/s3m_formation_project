package com.s3m.formation.api.dto;


import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SessionFormationAuditDto {

    private Long auditId;

    private SessionFormationStatut statutAvant;
    private SessionFormationStatut statutApres;

    private String modifiePar;

    private LocalDateTime dateModification;

    private String commentaire;
}
