package com.s3m.formation.api.dto;

import java.util.List;

public record CritereTemplateRequest(
        List<Entry> entries
) {
    public record Entry(
            Integer jour,        // ignored for UPSKILLING, required for TRAINING
            String libelle,
            String categorie
    ) {}
}