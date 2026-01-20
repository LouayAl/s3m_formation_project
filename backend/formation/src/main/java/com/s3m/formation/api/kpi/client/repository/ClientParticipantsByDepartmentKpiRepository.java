package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.ClientParticipantsByDepartmentKpiProjection;
import com.s3m.formation.domain.participation.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientParticipantsByDepartmentKpiRepository extends JpaRepository<Participation, Integer> {
    @Query("""
        SELECT
            d.nom AS departement,
            COUNT(DISTINCT e.idEmploye) AS nbParticipants
        FROM SessionFormation s
        JOIN s.participations p
        JOIN p.employe e
        JOIN e.departement d
        WHERE s.entreprise.idEntreprise = :clientId
        GROUP BY d.nom
        ORDER BY nbParticipants DESC
    """)
    List<ClientParticipantsByDepartmentKpiProjection> findByClientId(
            @Param("clientId") Integer clientId
    );

}
