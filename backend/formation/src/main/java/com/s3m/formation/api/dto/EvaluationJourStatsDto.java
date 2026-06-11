// api/dto/EvaluationJourStatsDto.java
package com.s3m.formation.api.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record EvaluationJourStatsDto(
        LocalDate jour,
        int totalReponses,
        double moyenneGlobale,
        Map<Integer, Double> moyennesParQuestion, // questionId -> avg score
        List<EvaluationAChaudResponseDto> reponses
) {}