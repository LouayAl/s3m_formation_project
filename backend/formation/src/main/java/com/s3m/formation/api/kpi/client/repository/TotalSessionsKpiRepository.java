package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.TotalSessionsProjection;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TotalSessionsKpiRepository extends JpaRepository<SessionFormation, Integer> {

    /**
     * Total sessions for a given client, filtered by years — native query for PostgreSQL.
     * Called after resolveYears() so years is never empty.
     */
    @Query(value = """
        SELECT COUNT(*) AS totalSessions
        FROM session_formation s
        WHERE (:clientId IS NULL OR s.id_entreprise = :clientId)
          AND EXTRACT(YEAR FROM s.date_debut)::INT = ANY(:years)
    """, nativeQuery = true)
    TotalSessionsProjection getTotalSessionsByClientAndYears(
            @Param("clientId") Integer clientId,
            @Param("years") Integer[] years
    );
}
