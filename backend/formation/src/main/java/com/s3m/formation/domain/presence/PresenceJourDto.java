package com.s3m.formation.domain.presence;

public record PresenceJourDto(
        Integer participationId,
        Integer idEmploye,
        String  nom,
        String  prenom,
        String  cin,
        String  matricule,
        Boolean present
) {
}
