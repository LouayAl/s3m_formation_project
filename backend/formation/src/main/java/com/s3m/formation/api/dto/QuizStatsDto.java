package com.s3m.formation.api.dto;
import java.util.List;
import java.util.Map;

public record QuizStatsDto(
        Integer idSession,
        String moduleFormation,
        String formateur,
        int totalReponses,
        int totalParticipants,
        double scoreMoyen,
        Map<String, Map<String, Integer>> distribution,
        List<QuizReponseDto> reponses
) {}