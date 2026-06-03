package com.s3m.formation.domain.planification;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BulkAddRequest(
        Integer    entrepriseId,
        LocalDate dateSession,
        int        count,          // how many sessions to create
        BigDecimal dHeures,        // hours per session (default 8)
        String     notes
) {}