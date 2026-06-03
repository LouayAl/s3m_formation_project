package com.s3m.formation.domain.planification;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SessionPlanifieeUpdateRequest(
        LocalDate dateSession,
        BigDecimal dHeures,
        String     notes
) {}
