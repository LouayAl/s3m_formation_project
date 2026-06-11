// api/dto/EvaluationAChaudRequest.java
package com.s3m.formation.api.dto;

import java.time.LocalDate;
import java.util.Map;

public record EvaluationAChaudRequest(
        Integer idSession,
        Integer idEmploye,
        LocalDate jourEvaluation,
        Map<Integer, Integer> reponses, // questionId -> score (1-4)
        String commentaire
) {}