package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.ClientHoursByDepartmentKpiProjection;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientHoursByDepartmentKpiRepository extends JpaRepository<SessionFormation, Integer> {

    @Query("""
        SELECT 
            d.nom AS departement,
            SUM(s.dHeures) AS totalHeures
        FROM SessionFormation s
        JOIN s.participations p
        JOIN p.employe e
        JOIN e.departement d
        WHERE s.entreprise.idEntreprise = :clientId
        GROUP BY d.nom
        ORDER BY totalHeures DESC
    """)
    List<ClientHoursByDepartmentKpiProjection> findByClientId(
            @Param("clientId") Integer clientId
    );


}
