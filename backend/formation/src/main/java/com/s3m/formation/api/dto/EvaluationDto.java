package com.s3m.formation.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record EvaluationDto(
        Integer id,
        Integer idSession,
        Integer idEmploye,
        String  employeNom,
        String  employePrenom,
        String  employeMatricule,
        Integer jour,
        String  presence,           // PRESENT | ABSENT | RETARD
        String  remarques,
        Map<Integer, Integer> scores, // criterionIndex -> score (1-4)
        Double  avgScore,
        LocalDateTime createdAt,
        BigDecimal dureeHeures
) {}