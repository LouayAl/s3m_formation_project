package com.s3m.formation.security.util;

public class SecurityUtils {

    private static final ThreadLocal<Integer> entrepriseIdHolder = new ThreadLocal<>();

    public static void setEntrepriseId(Integer entrepriseId) {
        entrepriseIdHolder.set(entrepriseId);
    }

    public static Integer getEntrepriseId() {
        return entrepriseIdHolder.get();
    }

    public static void clear() {
        entrepriseIdHolder.remove();
    }
}
