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
            SUM(s.dHeures) AS totalHeures
        FROM SessionFormation s
        JOIN s.formation f
        WHERE s.entreprise.idEntreprise = :clientId
        GROUP BY f.familleFormation
        ORDER BY totalHeures DESC
    """)
    List<ClientHoursByFamilleFormationKpiProjection> findByClientId(
            @Param("clientId") Integer clientId
    );

}
