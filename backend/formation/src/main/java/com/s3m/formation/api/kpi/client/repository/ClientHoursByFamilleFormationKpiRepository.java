package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.ClientHoursByFamilleFormationKpiProjection;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientHoursByFamilleFormationKpiRepository extends JpaRepository<SessionFormation, Integer> {
    @Query("""
        SELECT
            f.familleFormation AS familleFormation,
            SUM(f.dureeHeures) AS totalHeures
        FROM Participation p
        JOIN p.session s
        JOIN s.formation f
        GROUP BY f.familleFormation
        ORDER BY totalHeures DESC
    """)
    List<ClientHoursByFamilleFormationKpiProjection> findByClientId(
            @Param("clientId") Integer clientId
    );

    @Query(value = """
    SELECT
        f.famille_formation AS familleFormation,
        SUM(f.d_heures) AS totalHeures
    FROM participation p
    JOIN session_formation s ON p.id_session = s.id_session
    JOIN formation f ON s.id_formation = f.id_formation
    WHERE s.id_entreprise = :clientId
      AND EXTRACT(YEAR FROM s.date_debut)::INT = ANY(:years)
    GROUP BY f.famille_formation
    ORDER BY totalHeures DESC
""", nativeQuery = true)
    List<ClientHoursByFamilleFormationKpiProjection> findByClientIdAndYears(
            @Param("clientId") Integer clientId,
            @Param("years") Integer[] years
    );
}
