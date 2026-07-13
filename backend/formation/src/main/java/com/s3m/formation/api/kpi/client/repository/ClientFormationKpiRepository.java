package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.TotalFormationHoursProjection;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientFormationKpiRepository extends JpaRepository<SessionFormation, Integer> {

    @Query(value = """
        SELECT COALESCE(SUM(f.d_heures), 0) AS totalHeures
        FROM participation p
        JOIN session_formation s ON p.id_session  = s.id_session
        JOIN formation f         ON s.id_formation = f.id_formation
        WHERE (:clientId IS NULL OR s.id_entreprise = :clientId)
          AND EXTRACT(YEAR FROM s.date_debut)::INT = ANY(:years)
    """, nativeQuery = true)
    TotalFormationHoursProjection getTotalFormationHours(
            @Param("clientId") Integer clientId,
            @Param("years") Integer[] years
    );

    @Query(value = """
        SELECT DISTINCT EXTRACT(YEAR FROM date_debut)::INT AS year
        FROM session_formation
        WHERE (:clientId IS NULL OR id_entreprise = :clientId)
          AND date_debut IS NOT NULL
        ORDER BY year DESC
    """, nativeQuery = true)
    List<Integer> findDistinctYearsByClientId(@Param("clientId") Integer clientId);

    // ─── Monthly — no year filter (all time) ────────────────────────────────────

    @Query(value = """
        WITH monthly_totals AS (
            SELECT
                TO_CHAR(s.date_debut, 'Mon YYYY') AS mois,
                f.module AS formation,
                SUM(f.d_heures) AS total_heures
            FROM participation p
            JOIN session_formation s ON p.id_session = s.id_session
            JOIN formation f ON s.id_formation = f.id_formation
            WHERE (:entrepriseId IS NULL OR s.id_entreprise = :entrepriseId)
            GROUP BY mois, f.module
        ),
        top_per_month AS (
            SELECT DISTINCT ON (mois)
                mois, formation AS top_module, total_heures
            FROM monthly_totals
            ORDER BY mois, total_heures DESC
        )
        SELECT
            m.mois,
            t.top_module,
            COALESCE(SUM(CASE WHEN m.formation = t.top_module THEN m.total_heures ELSE 0 END), 0) AS topHours,
            COALESCE(SUM(CASE WHEN m.formation != t.top_module THEN m.total_heures ELSE 0 END), 0) AS autresHours
        FROM monthly_totals m
        JOIN top_per_month t ON m.mois = t.mois
        GROUP BY m.mois, t.top_module
        ORDER BY TO_DATE(m.mois, 'Mon YYYY')
    """, nativeQuery = true)
    List<Object[]> getTotalGrowthByMonthForEntreprise(
            @Param("entrepriseId") Integer entrepriseId
    );

    // ─── Monthly — single year (label = "Jan YYYY") ──────────────────────────────

    @Query(value = """
        WITH monthly_totals AS (
            SELECT
                TO_CHAR(s.date_debut, 'Mon YYYY') AS mois,
                f.module AS formation,
                SUM(f.d_heures) AS total_heures
            FROM participation p
            JOIN session_formation s ON p.id_session = s.id_session
            JOIN formation f ON s.id_formation = f.id_formation
            WHERE (:entrepriseId IS NULL OR s.id_entreprise = :entrepriseId)
              AND EXTRACT(YEAR FROM s.date_debut)::INT = :year
            GROUP BY mois, f.module
        ),
        top_per_month AS (
            SELECT DISTINCT ON (mois)
                mois, formation AS top_module, total_heures
            FROM monthly_totals
            ORDER BY mois, total_heures DESC
        )
        SELECT
            m.mois,
            t.top_module,
            COALESCE(SUM(CASE WHEN m.formation = t.top_module THEN m.total_heures ELSE 0 END), 0) AS topHours,
            COALESCE(SUM(CASE WHEN m.formation != t.top_module THEN m.total_heures ELSE 0 END), 0) AS autresHours
        FROM monthly_totals m
        JOIN top_per_month t ON m.mois = t.mois
        GROUP BY m.mois, t.top_module
        ORDER BY TO_DATE(m.mois, 'Mon YYYY')
    """, nativeQuery = true)
    List<Object[]> getTotalGrowthByMonthForEntrepriseAndYear(
            @Param("entrepriseId") Integer entrepriseId,
            @Param("year") Integer year
    );

    // ─── Monthly — multiple years (label = "Jan", sum across years) ─────────────

    @Query(value = """
        WITH monthly_totals AS (
            SELECT
                TO_CHAR(s.date_debut, 'Mon') AS mois,
                EXTRACT(MONTH FROM s.date_debut)::INT AS mois_num,
                f.module AS formation,
                SUM(f.d_heures) AS total_heures
            FROM participation p
            JOIN session_formation s ON p.id_session = s.id_session
            JOIN formation f ON s.id_formation = f.id_formation
            WHERE (:entrepriseId IS NULL OR s.id_entreprise = :entrepriseId)
              AND EXTRACT(YEAR FROM s.date_debut)::INT = ANY(:years)
            GROUP BY TO_CHAR(s.date_debut, 'Mon'), EXTRACT(MONTH FROM s.date_debut)::INT, f.module
        ),
        top_per_month AS (
            SELECT DISTINCT ON (mois_num)
                mois, mois_num, formation AS top_module, total_heures
            FROM monthly_totals
            ORDER BY mois_num, total_heures DESC
        )
        SELECT
            m.mois,
            t.top_module,
            COALESCE(SUM(CASE WHEN m.formation = t.top_module THEN m.total_heures ELSE 0 END), 0) AS topHours,
            COALESCE(SUM(CASE WHEN m.formation != t.top_module THEN m.total_heures ELSE 0 END), 0) AS autresHours
        FROM monthly_totals m
        JOIN top_per_month t ON m.mois_num = t.mois_num
        GROUP BY m.mois, t.top_module, t.mois_num
        ORDER BY t.mois_num
    """, nativeQuery = true)
    List<Object[]> getTotalGrowthByMonthForEntrepriseAndYears(
            @Param("entrepriseId") Integer entrepriseId,
            @Param("years") Integer[] years
    );

    // ─── Daily (drilldown) — unchanged ───────────────────────────────────────────

    @Query(value = """
        WITH session_hours AS (
            SELECT
                DATE(s.date_debut) AS jour,
                f.module AS formation,
                s.id_session,
                COUNT(p.id_employe) * f.d_heures AS session_total_hours
            FROM session_formation s
            JOIN formation f ON s.id_formation = f.id_formation
            JOIN participation p ON p.id_session = s.id_session
            WHERE (:entrepriseId IS NULL OR s.id_entreprise = :entrepriseId)
              AND s.date_debut IS NOT NULL
              AND TO_CHAR(s.date_debut, 'YYYY-MM') = :month
            GROUP BY DATE(s.date_debut), f.module, s.id_session, f.d_heures
        ),
        daily_totals AS (
            SELECT jour, formation, SUM(session_total_hours) AS total_heures
            FROM session_hours
            GROUP BY jour, formation
        ),
        top_per_day AS (
            SELECT DISTINCT ON (jour)
                jour, formation AS top_module, total_heures
            FROM daily_totals
            ORDER BY jour, total_heures DESC
        )
        SELECT
            d.jour,
            t.top_module,
            SUM(CASE WHEN d.formation = t.top_module THEN d.total_heures ELSE 0 END) AS top_hours,
            SUM(CASE WHEN d.formation != t.top_module THEN d.total_heures ELSE 0 END) AS autres_hours
        FROM daily_totals d
        JOIN top_per_day t ON d.jour = t.jour
        GROUP BY d.jour, t.top_module
        ORDER BY d.jour
    """, nativeQuery = true)
    List<Object[]> getTotalGrowthByDayForEntreprise(
            @Param("entrepriseId") Integer entrepriseId,
            @Param("month") String month
    );

    // ─── Yearly — no year filter (all time) ─────────────────────────────────────

    @Query(value = """
        WITH session_hours AS (
            SELECT
                EXTRACT(YEAR FROM s.date_debut)::INT AS annee,
                f.module AS formation,
                s.id_session,
                COUNT(p.id_employe) * f.d_heures AS session_total_hours
            FROM session_formation s
            JOIN formation f ON s.id_formation = f.id_formation
            JOIN participation p ON p.id_session = s.id_session
            WHERE (:entrepriseId IS NULL OR s.id_entreprise = :entrepriseId)
              AND s.date_debut IS NOT NULL
            GROUP BY annee, f.module, s.id_session, f.d_heures
        ),
        yearly_totals AS (
            SELECT annee, formation, SUM(session_total_hours) AS total_heures
            FROM session_hours
            GROUP BY annee, formation
        ),
        top_per_year AS (
            SELECT DISTINCT ON (annee)
                annee, formation AS top_module, total_heures
            FROM yearly_totals
            ORDER BY annee, total_heures DESC
        )
        SELECT
            y.annee,
            t.top_module,
            SUM(CASE WHEN y.formation = t.top_module THEN y.total_heures ELSE 0 END) AS topHours,
            SUM(CASE WHEN y.formation != t.top_module THEN y.total_heures ELSE 0 END) AS autresHours
        FROM yearly_totals y
        JOIN top_per_year t ON y.annee = t.annee
        GROUP BY y.annee, t.top_module
        ORDER BY y.annee
    """, nativeQuery = true)
    List<Object[]> getTotalGrowthByYearForEntreprise(
            @Param("entrepriseId") Integer entrepriseId
    );

    // ─── Yearly — filtered by selected years ─────────────────────────────────────

    @Query(value = """
        WITH session_hours AS (
            SELECT
                EXTRACT(YEAR FROM s.date_debut)::INT AS annee,
                f.module AS formation,
                s.id_session,
                COUNT(p.id_employe) * f.d_heures AS session_total_hours
            FROM session_formation s
            JOIN formation f ON s.id_formation = f.id_formation
            JOIN participation p ON p.id_session = s.id_session
            WHERE (:entrepriseId IS NULL OR s.id_entreprise = :entrepriseId)
              AND s.date_debut IS NOT NULL
              AND EXTRACT(YEAR FROM s.date_debut)::INT = ANY(:years)
            GROUP BY annee, f.module, s.id_session, f.d_heures
        ),
        yearly_totals AS (
            SELECT annee, formation, SUM(session_total_hours) AS total_heures
            FROM session_hours
            GROUP BY annee, formation
        ),
        top_per_year AS (
            SELECT DISTINCT ON (annee)
                annee, formation AS top_module, total_heures
            FROM yearly_totals
            ORDER BY annee, total_heures DESC
        )
        SELECT
            y.annee,
            t.top_module,
            SUM(CASE WHEN y.formation = t.top_module THEN y.total_heures ELSE 0 END) AS topHours,
            SUM(CASE WHEN y.formation != t.top_module THEN y.total_heures ELSE 0 END) AS autresHours
        FROM yearly_totals y
        JOIN top_per_year t ON y.annee = t.annee
        GROUP BY y.annee, t.top_module
        ORDER BY y.annee
    """, nativeQuery = true)
    List<Object[]> getTotalGrowthByYearForEntrepriseAndYears(
            @Param("entrepriseId") Integer entrepriseId,
            @Param("years") Integer[] years
    );
}
