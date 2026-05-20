package com.s3m.formation.api.dto;

import java.util.List;

public record SessionCritereRequest(
        List<CritereEntry> criteres
) {
    public record CritereEntry(
            String libelle,
            String categorie  // nullable — null means it's a regular critere without category
    ) {}
}