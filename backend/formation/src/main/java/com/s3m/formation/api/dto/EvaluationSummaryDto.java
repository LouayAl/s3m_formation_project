package com.s3m.formation.api.dto;

import java.time.LocalDateTime;

public record EvaluationSummaryDto(
        Integer idSession,
        String referenceSession,
        String moduleFormation,
        String formateur,
        int totalReponses,
        int totalParticipants,
        double moyenneGlobale,
        LocalDateTime derniereSoumission
) {}