package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.ClientEfficaciteKpiProjection;
import com.s3m.formation.domain.EvaluationAFroid.EvaluationAFroid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientEfficaciteKpiRepository extends JpaRepository<EvaluationAFroid, Integer> {

    @Query("""
        SELECT 
            COUNT(e) AS totalEvals,
            COUNT(e.tauxEfficacite) AS evalCount,
            COALESCE(SUM(e.tauxEfficacite), 0) AS sumTaux,
            MAX(e.dateEvaluationAFroid) AS lastEvalDate
        FROM EvaluationAFroid e
        WHERE e.participation.session.fournisseur.idEntreprise = :clientId
    """)
    ClientEfficaciteKpiProjection computeEfficacite(@Param("clientId") Integer clientId);
}
