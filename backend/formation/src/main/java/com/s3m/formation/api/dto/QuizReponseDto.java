// api/dto/QuizReponseDto.java
package com.s3m.formation.api.dto;
import java.time.LocalDateTime;
import java.util.Map;

public record QuizReponseDto(
        Integer idReponse,
        String nomEmploye,
        Map<String, String> reponses,
        Integer score,
        LocalDateTime soumisLe,
        LocalDateTime debutLe
) {}