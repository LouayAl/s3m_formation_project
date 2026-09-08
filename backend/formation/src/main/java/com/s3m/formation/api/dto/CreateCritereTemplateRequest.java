package com.s3m.formation.api.dto;

public record CreateCritereTemplateRequest(
        String nom,
        String type // "UPSKILLING" | "TRAINING"
) {}