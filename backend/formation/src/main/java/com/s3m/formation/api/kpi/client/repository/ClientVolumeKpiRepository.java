package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.ClientVolumeKpiProjection;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientVolumeKpiRepository extends JpaRepository<SessionFormation, Integer> {

    @Query("""
        SELECT
            COUNT(DISTINCT s.idSession) AS totalSessions,
            COUNT(p.idParticipation)    AS totalParticipants,
            COALESCE(SUM(s.dJours), 0)  AS totalJours,
            COALESCE(SUM(s.dHeures), 0) AS totalHeures
        FROM SessionFormation s
        LEFT JOIN Participation p ON p.session.idSession = s.idSession
        WHERE s.fournisseur.idEntreprise = :clientId
    """)
    ClientVolumeKpiProjection computeVolume(@Param("clientId") Integer clientId);
}
