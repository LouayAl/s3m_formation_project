package com.s3m.formation.api.dto;

import java.util.List;

public record CritereTemplateEntriesRequest(
        List<Entry> entries
) {
    public record Entry(
            Integer jour,
            String libelle,
            String categorie
    ) {}
}