package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.ClientFinancierByRemboursementProjection;
import com.s3m.formation.api.kpi.client.projection.ClientFinancierKpiProjection;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientFinancierKpiRepository extends JpaRepository<SessionFormation, Integer> {

    @Query(value = """
        SELECT
            COALESCE(SUM(cf.cout_total), 0)                                                             AS coutTotal,
            COALESCE(SUM(CASE WHEN cf.remboursement = 'OUI' THEN cf.cout_total ELSE 0 END), 0)         AS coutRembourse,
            COALESCE(SUM(CASE WHEN cf.remboursement = 'OUI' THEN 0 ELSE cf.cout_total END), 0)         AS coutNonRembourse,
            COALESCE(SUM(sf.d_jours), 0)                                                                AS totalJours,
            COUNT(p.id_participation)                                                                   AS totalParticipants
        FROM session_formation sf
        LEFT JOIN cout_formation cf ON cf.id_session = sf.id_session
        LEFT JOIN participation p   ON p.id_session  = sf.id_session
        WHERE (:clientId IS NULL OR sf.id_entreprise = :clientId)
          AND EXTRACT(YEAR FROM sf.date_debut)::INT = ANY(:years)
    """, nativeQuery = true)
    ClientFinancierKpiProjection computeFinancier(
            @Param("clientId") Integer clientId,
            @Param("years") Integer[] years
    );

    @Query(value = """
        SELECT
            cf.remboursement                                        AS remboursement,
            SUM(sf.d_heures)                                        AS totalHeures,
            COUNT(p.id_participation)                               AS totalParticipants,
            SUM(sf.d_heures) * COUNT(p.id_participation)            AS totalHeuresParticipant
        FROM cout_formation cf
        JOIN session_formation sf ON sf.id_session = cf.id_session
        JOIN participation p      ON p.id_session  = sf.id_session
        WHERE (:clientId IS NULL OR sf.id_entreprise = :clientId)
          AND EXTRACT(YEAR FROM sf.date_debut)::INT = ANY(:years)
          AND cf.remboursement IN ('CSF', 'Emergence')
        GROUP BY cf.remboursement
        ORDER BY cf.remboursement
    """, nativeQuery = true)
    List<ClientFinancierByRemboursementProjection> computeFinancierByRemboursement(
            @Param("clientId") Integer clientId,
            @Param("years") Integer[] years
    );
}
