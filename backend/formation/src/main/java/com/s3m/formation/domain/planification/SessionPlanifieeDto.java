package com.s3m.formation.domain.planification;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SessionPlanifieeDto(
        Integer   id,
        LocalDate dateSession,
        BigDecimal dHeures,
        String    notes
) {}