package com.s3m.formation.api.kpi.client.dto;

import java.time.LocalDate;
import java.util.List;

public record VisibiliteSessionDto(
        Integer idSession,
        String referenceSession,
        String moduleFormation,
        String formateur,
        String entreprise,
        LocalDate dateDebut,
        LocalDate dateFin,
        String lieu,
        int nbParticipants,
        List<LocalDate> jours
) {}
