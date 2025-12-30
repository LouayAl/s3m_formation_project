package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.ClientFormationKpiProjection;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientFormationKpiRepository extends JpaRepository<SessionFormation, Integer> {

    @Query("""
        SELECT
            COUNT(DISTINCT s.formation.module) AS totalFormations,
            (SELECT s1.formation.module 
             FROM SessionFormation s1
             WHERE s1.fournisseur.idEntreprise = :clientId
             GROUP BY s1.formation.module
             ORDER BY COUNT(s1) DESC
             LIMIT 1) AS formationLaPlusSuivie,
            (SELECT s2.formation.familleFormation
             FROM SessionFormation s2
             WHERE s2.fournisseur.idEntreprise = :clientId
             GROUP BY s2.formation.familleFormation
             ORDER BY COUNT(s2) DESC
             LIMIT 1) AS famillePrincipale,
            (SELECT COALESCE(SUM(CASE WHEN LOWER(s3.formation.interneExterne) = 'interne' THEN 1 ELSE 0 END) * 100.0 / COUNT(s3), 0)
             FROM SessionFormation s3
             WHERE s3.fournisseur.idEntreprise = :clientId) AS pourcentageInterne,
            (SELECT COALESCE(SUM(CASE WHEN LOWER(s4.formation.interneExterne) = 'externe' THEN 1 ELSE 0 END) * 100.0 / COUNT(s4), 0)
             FROM SessionFormation s4
             WHERE s4.fournisseur.idEntreprise = :clientId) AS pourcentageExterne
        FROM SessionFormation s
        WHERE s.fournisseur.idEntreprise = :clientId
    """)
    ClientFormationKpiProjection computeFormations(@Param("clientId") Integer clientId);
}
