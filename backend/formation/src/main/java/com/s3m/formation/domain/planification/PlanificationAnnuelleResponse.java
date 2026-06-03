package com.s3m.formation.domain.planification;

import java.util.List;

public record PlanificationAnnuelleResponse(
        int                      annee,
        Integer                  entrepriseId,
        List<SessionPlanifieeDto> sessions,      // raw planned sessions
        List<MonthSummary>       planned,        // 12 monthly buckets (planned)
        List<MonthSummary>       actual,          // 12 monthly buckets (TERMINEE sessions)
        boolean                  yearChanged
) {
    public record MonthSummary(
            int    month,          // 1 = Jan … 12 = Dec
            String monthLabel,
            int    sessionCount,
            double heures
    ) {}
}
