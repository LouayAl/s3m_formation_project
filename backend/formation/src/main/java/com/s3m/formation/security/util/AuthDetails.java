package com.s3m.formation.security.util;

public class AuthDetails {

    private final Integer entrepriseId;
    private final Integer departementId; // null = unrestricted (Admin)

    public AuthDetails(Integer entrepriseId, Integer departementId) {
        this.entrepriseId = entrepriseId;
        this.departementId = departementId;
    }

    public Integer getEntrepriseId() {
        return entrepriseId;
    }

    public Integer getDepartementId() {
        return departementId;
    }
}