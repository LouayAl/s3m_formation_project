package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.ClientParticipantsByDepartmentKpiProjection;
import com.s3m.formation.domain.participation.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientParticipantsByDepartmentKpiRepository extends JpaRepository<Participation, Integer> {
    @Query(value = """
        SELECT d.nom AS departement, COUNT(*) AS nbParticipants
        FROM participation p
        JOIN employe e ON p.id_employe = e.id_employe
        JOIN departement d ON e.id_departement = d.id_departement
        WHERE (:clientId IS NULL OR e.id_entreprise = :clientId)
        GROUP BY d.nom
        ORDER BY nbParticipants DESC
    """, nativeQuery = true)
    List<ClientParticipantsByDepartmentKpiProjection> findByClientId(
            @Param("clientId") Integer clientId
    );

    @Query(value = """
    SELECT d.nom AS departement, COUNT(*) AS nbParticipants
    FROM participation p
    JOIN employe e ON p.id_employe = e.id_employe
    JOIN departement d ON e.id_departement = d.id_departement
    JOIN session_formation s ON p.id_session = s.id_session
    WHERE (:clientId IS NULL OR e.id_entreprise = :clientId)
      AND EXTRACT(YEAR FROM s.date_debut) IN (:years)
    GROUP BY d.nom
    ORDER BY nbParticipants DESC
""", nativeQuery = true)
    List<ClientParticipantsByDepartmentKpiProjection> findByClientIdAndYears(
            @Param("clientId") Integer clientId,
            @Param("years") Integer[] years
    );
}
