// api/dto/EvaluationAChaudStatsDto.java
package com.s3m.formation.api.dto;

import java.util.List;
import java.util.Map;

public record EvaluationAChaudStatsDto(
        Integer idSession,
        String referenceSession,
        String moduleFormation,
        String formateur,
        int totalReponses,
        int totalParticipants,
        double moyenneGlobale,
        Map<Integer, Double> moyennesParQuestion,   // questionId -> avg score
        List<EvaluationAChaudResponseDto> reponses
) {}