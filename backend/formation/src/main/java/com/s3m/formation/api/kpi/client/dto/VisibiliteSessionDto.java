package com.s3m.formation.api.kpi.client.dto;

import java.time.LocalDate;

public record VisibiliteSessionDto(
        Integer idSession,
        String referenceSession,
        String moduleFormation,
        String formateur,
        String entreprise,
        LocalDate dateDebut,
        LocalDate dateFin,
        String lieu,
        int nbParticipants
) {}