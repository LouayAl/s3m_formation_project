package com.s3m.formation.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtils {

    private static AuthDetails getDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof AuthDetails details) {
            return details;
        }
        return null;
    }

    public static Integer getEntrepriseId() {
        AuthDetails details = getDetails();
        return details != null ? details.getEntrepriseId() : null;
    }

    public static Integer getDepartementId() {
        AuthDetails details = getDetails();
        return details != null ? details.getDepartementId() : null;
    }

    public static String getEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}