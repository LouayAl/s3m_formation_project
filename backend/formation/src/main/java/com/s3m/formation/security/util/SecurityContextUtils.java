package com.s3m.formation.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtils {

    public static Integer getEntrepriseId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? (Integer) auth.getDetails() : null;
    }

    public static String getEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
