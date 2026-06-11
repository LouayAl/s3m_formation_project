// api/dto/EvaluationAChaudResponseDto.java
package com.s3m.formation.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record EvaluationAChaudResponseDto(
        Integer idEvalChaud,
        Integer idSession,
        String nomEmploye,
        LocalDate jourEvaluation,
        List<EvaluationReponseDto> reponses,
        String commentaire,
        LocalDateTime soumisLe
) {}