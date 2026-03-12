package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.*;
import com.s3m.formation.domain.employe.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientPopulationKpiRepository extends JpaRepository<Employe, Integer> {

    /**
     * CSP breakdown — native query, uses = ANY(:years) for PostgreSQL array binding.
     */
    @Query(value = """
        SELECT COALESCE(e.csp, 'Non renseigné') AS label, COUNT(DISTINCT e.id_employe) AS count
        FROM participation p
        JOIN employe e        ON p.id_employe = e.id_employe
        JOIN session_formation s ON p.id_session = s.id_session
        WHERE s.id_entreprise = :clientId
          AND EXTRACT(YEAR FROM s.date_debut)::INT = ANY(:years)
        GROUP BY COALESCE(e.csp, 'Non renseigné')
    """, nativeQuery = true)
    List<RepartitionItemProjection> countByCsp(
            @Param("clientId") Integer clientId,
            @Param("years") Integer[] years
    );

    /**
     * Fonction / TypeContrat / Genre breakdowns query the Employe table directly
     * (no date filter — they are workforce snapshots, not tied to training sessions).
     */
    @Query("""
        SELECT COALESCE(e.fonction, 'Non renseigné') AS label, COUNT(e) AS count
        FROM Employe e
        WHERE e.entreprise.idEntreprise = :clientId
        GROUP BY COALESCE(e.fonction, 'Non renseigné')
    """)
    List<RepartitionItemProjection> countByFonction(@Param("clientId") Integer clientId);

    @Query("""
        SELECT COALESCE(e.typeContrat, 'Non renseigné') AS label, COUNT(e) AS count
        FROM Employe e
        WHERE e.entreprise.idEntreprise = :clientId
        GROUP BY COALESCE(e.typeContrat, 'Non renseigné')
    """)
    List<RepartitionItemProjection> countByTypeContrat(@Param("clientId") Integer clientId);

    @Query("""
        SELECT COALESCE(e.f_h, 'Non renseigné') AS label, COUNT(e) AS count
        FROM Employe e
        WHERE e.entreprise.idEntreprise = :clientId
        GROUP BY COALESCE(e.f_h, 'Non renseigné')
    """)
    List<RepartitionItemProjection> countByGenre(@Param("clientId") Integer clientId);

    /**
     * Gender breakdown by department — native query.
     */
    @Query(value = """
        SELECT
            COALESCE(d.nom, 'Non défini')  AS departement,
            COALESCE(e.f_h, 'N')           AS genre,
            COUNT(p.id_participation)      AS nombre
        FROM participation p
        JOIN employe e           ON p.id_employe  = e.id_employe
        LEFT JOIN departement d  ON e.id_departement = d.id_departement
        JOIN session_formation s ON p.id_session  = s.id_session
        WHERE s.id_entreprise = :clientId
          AND EXTRACT(YEAR FROM s.date_debut)::INT = ANY(:years)
        GROUP BY d.nom, e.f_h
        ORDER BY d.nom, e.f_h
    """, nativeQuery = true)
    List<ClientGenderByDepartmentKpiProjection> getGenderByDepartmentForClient(
            @Param("clientId") Integer clientId,
            @Param("years") Integer[] years
    );

    /**
     * Training hours by gender — native query.
     */
    @Query(value = """
        SELECT
            COALESCE(e.f_h, 'N')            AS label,
            SUM(s.d_heures)                 AS totalHeures,
            COUNT(DISTINCT e.id_employe)    AS nombreEmployes
        FROM participation p
        JOIN employe e           ON p.id_employe = e.id_employe
        JOIN session_formation s ON p.id_session = s.id_session
        WHERE s.id_entreprise = :clientId
          AND EXTRACT(YEAR FROM s.date_debut)::INT = ANY(:years)
        GROUP BY e.f_h
    """, nativeQuery = true)
    List<GenderHoursKpiProjection> getTrainingHoursByGender(
            @Param("clientId") Integer clientId,
            @Param("years") Integer[] years
    );

    /**
     * Training hours by CSP — native query.
     */
    @Query(value = """
        SELECT
            CASE
                WHEN e.csp IS NULL OR e.csp = '' OR e.csp = '#REF!' THEN 'Non défini'
                ELSE e.csp
            END                             AS csp,
            SUM(s.d_heures)                 AS totalHeures,
            COUNT(DISTINCT e.id_employe)    AS nombreEmployes
        FROM participation p
        JOIN employe e           ON p.id_employe = e.id_employe
        JOIN session_formation s ON p.id_session = s.id_session
        WHERE s.id_entreprise = :clientId
          AND EXTRACT(YEAR FROM s.date_debut)::INT = ANY(:years)
        GROUP BY
            CASE
                WHEN e.csp IS NULL OR e.csp = '' OR e.csp = '#REF!' THEN 'Non défini'
                ELSE e.csp
            END
    """, nativeQuery = true)
    List<CspHoursKpiProjection> getTrainingHoursByCsp(
            @Param("clientId") Integer clientId,
            @Param("years") Integer[] years
    );

    /**
     * Total distinct participants — native query.
     */
    @Query(value = """
        SELECT COUNT(DISTINCT p.id_employe) AS totalParticipants
        FROM participation p
        JOIN session_formation s ON p.id_session = s.id_session
        WHERE s.id_entreprise = :clientId
          AND EXTRACT(YEAR FROM s.date_debut)::INT = ANY(:years)
    """, nativeQuery = true)
    TotalParticipantsKpiProjection getTotalParticipants(
            @Param("clientId") Integer clientId,
            @Param("years") Integer[] years
    );
}