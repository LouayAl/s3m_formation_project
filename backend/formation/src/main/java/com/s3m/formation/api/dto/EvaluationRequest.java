package com.s3m.formation.api.dto;

import java.util.Map;

public record EvaluationRequest(
        Integer idSession,
        Integer idEmploye,
        Integer jour,
        String  presence,   // PRESENT | ABSENT | RETARD  (optional, defaults to PRESENT)
        String  remarques,  // optional
        Map<Integer, Integer> scores // criterionIndex -> score 1-4 (can be empty)
) {}