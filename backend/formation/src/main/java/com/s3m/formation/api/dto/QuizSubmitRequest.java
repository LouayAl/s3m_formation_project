// api/dto/QuizSubmitRequest.java
package com.s3m.formation.api.dto;
import java.time.LocalDateTime;
import java.util.Map;

public record QuizSubmitRequest(
        Integer idSession,
        Integer idEmploye,
        Map<String, String> reponses,  // questionId (as string) -> answer
        LocalDateTime debutLe
) {}