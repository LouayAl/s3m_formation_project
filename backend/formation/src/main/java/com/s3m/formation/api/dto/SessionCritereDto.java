package com.s3m.formation.api.dto;

public record SessionCritereDto(
        Integer id,
        Integer jour,
        Integer critereIndex,
        String libelle,
        String categorie
) {
}