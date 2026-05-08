package com.s3m.formation.api.dto;

import java.util.List;

public record DailyProgramDto(
        Integer id,
        Integer sessionId,
        Integer jour,
        String commentaire,
        List<DailyProgramEntryDto> entries
) {}
