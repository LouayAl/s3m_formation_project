// src/main/java/com/s3m/formation/domain/planification/PlanAnnuelRequest.java
package com.s3m.formation.domain.planification;

/**
 * Request body for POST /api/planification — admin sets annual targets per month.
 */
public record PlanAnnuelRequest(
        Integer annee,
        Integer entrepriseId,
        int jan, int fev, int mar, int avr,
        int mai, int jui, int jul, int aou,
        int sep, int oct, int nov, int dec
) {}