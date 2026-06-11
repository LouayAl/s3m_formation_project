// api/dto/EvaluationAChaudStatsDto.java
package com.s3m.formation.api.dto;

import java.util.List;

public record EvaluationAChaudStatsDto(
        Integer idSession,
        String moduleFormation,
        int totalReponses,
        int totalParticipants,
        double moyenneGlobale,
        List<EvaluationJourStatsDto> parJour
) {}