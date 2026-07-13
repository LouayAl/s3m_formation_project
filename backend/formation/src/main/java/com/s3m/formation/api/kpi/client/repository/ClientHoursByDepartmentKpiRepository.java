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
        WHERE (:clientId IS NULL OR s.entreprise.idEntreprise = :clientId)
        GROUP BY d.nom
        ORDER BY totalHeures DESC
    """)
    List<ClientHoursByDepartmentKpiProjection> findByClientId(
            @Param("clientId") Integer clientId
    );

    @Query(value = """
        SELECT d.nom AS departement,
               SUM(s.d_heures) AS totalHeures
        FROM session_formation s
        JOIN participation p ON s.id_session = p.id_session
        JOIN employe e ON p.id_employe = e.id_employe
        JOIN departement d ON e.id_departement = d.id_departement
        WHERE (:clientId IS NULL OR s.id_entreprise = :clientId)
          AND EXTRACT(YEAR FROM s.date_debut)::INT = ANY(:years)
        GROUP BY d.nom
        ORDER BY totalHeures DESC
    """, nativeQuery = true)
    List<ClientHoursByDepartmentKpiProjection> findByClientIdAndYears(
            @Param("clientId") Integer clientId,
            @Param("years") Integer[] years
    );

}
