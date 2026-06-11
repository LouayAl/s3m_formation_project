// api/dto/EvaluationReponseDto.java
package com.s3m.formation.api.dto;

public record EvaluationReponseDto(
        Integer idQuestion,
        Integer score
) {}