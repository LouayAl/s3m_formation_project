package com.s3m.formation.api.dto;

import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateSessionRequest(
        BigDecimal dHeures,
        BigDecimal dJours,
        LocalDate dateDebut,
        LocalDate dateFin,
        Integer idFormateur,
        Integer idEntreprise,
        Integer idFournisseur,
        Integer idFormation,
        SessionFormationStatut statut
) {
}
