package com.s3m.formation.api.dto;

import java.time.LocalDateTime;

public record DailyProgramEntryDto(
        Integer id,
        LocalDateTime dateDebut,
        LocalDateTime dateFin,
        String activite,
        Integer position
) {}
