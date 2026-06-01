// src/main/java/com/s3m/formation/domain/planification/PlanAnnuelDto.java
package com.s3m.formation.domain.planification;

import java.util.List;

/**
 * Response DTO — contains both the planned targets and the actual (TERMINEE) counts,
 * one entry per month so the frontend can drive a chart directly.
 */
public record PlanAnnuelDto(
        Integer annee,
        Integer entrepriseId,
        String  entrepriseNom,
        List<MonthData> months  // 12 entries, index 0 = January
) {
    public record MonthData(
            int    month,        // 1-12
            String label,        // "Jan", "Fév", …
            int    planifie,     // target set by admin
            int    realise       // TERMINEE sessions that month
    ) {}
}