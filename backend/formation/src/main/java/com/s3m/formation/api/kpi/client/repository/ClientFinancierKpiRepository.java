package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.ClientFinancierByRemboursementProjection;
import com.s3m.formation.api.kpi.client.projection.ClientFinancierKpiProjection;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientFinancierKpiRepository extends JpaRepository<SessionFormation, Integer> {

    @Query("""
        SELECT
            COALESCE(SUM(c.coutTotal), 0) AS coutTotal,
            COALESCE(SUM(CASE WHEN c.remboursement = 'OUI' THEN c.coutTotal ELSE 0 END), 0) AS coutRembourse,
            COALESCE(SUM(CASE WHEN c.remboursement = 'OUI' THEN 0 ELSE c.coutTotal END), 0) AS coutNonRembourse,
            COALESCE(SUM(s.dJours), 0) AS totalJours,
            COUNT(p.idParticipation) AS totalParticipants
        FROM SessionFormation s
        LEFT JOIN CoutFormation c ON c.session.idSession = s.idSession
        LEFT JOIN Participation p ON p.session.idSession = s.idSession
        WHERE s.fournisseur.idEntreprise = :clientId
    """)
    ClientFinancierKpiProjection computeFinancier(@Param("clientId") Integer clientId);

    @Query("""
    SELECT
        c.remboursement AS remboursement,
        SUM(s.dHeures) AS totalHeures,
        COUNT(p.idParticipation) AS totalParticipants,
        SUM(s.dHeures) * COUNT(p.idParticipation) AS totalHeuresParticipant
    FROM CoutFormation c
    JOIN c.session s
    JOIN Participation p ON p.session.idSession = s.idSession
    WHERE c.remboursement IN ('CSF', 'Emergence')
    GROUP BY c.remboursement
    ORDER BY c.remboursement
""")
    List<ClientFinancierByRemboursementProjection> computeFinancierByRemboursement();

}
