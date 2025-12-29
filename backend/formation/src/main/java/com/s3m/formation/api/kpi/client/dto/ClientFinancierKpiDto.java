package com.s3m.formation.api.kpi.client.dto;

import java.math.BigDecimal;

public record ClientFinancierKpiDto(
        BigDecimal coutTotalFormation,
        BigDecimal coutMoyenParJour,
        BigDecimal coutMoyenParParticipant,
        BigDecimal montantRembourse,
        BigDecimal montantNonRembourse
) {
}
