package com.s3m.formation.api.dto;

import java.util.List;

public record DailyProgramRequest(
        String commentaire,
        List<DailyProgramEntryDto> entries
) {}
