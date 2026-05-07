package com.s3m.formation.domain.evaluation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {

    // All evaluations for a session, ordered by day
    List<Evaluation> findBySession_IdSessionOrderByJourAsc(Integer idSession);

    // All evaluations for one participant in a session, ordered by day
    List<Evaluation> findBySession_IdSessionAndEmploye_IdEmployeOrderByJourAsc(
            Integer idSession, Integer idEmploye
    );

    // Exact match for upsert logic
    Optional<Evaluation> findBySession_IdSessionAndEmploye_IdEmployeAndJour(
            Integer idSession, Integer idEmploye, Integer jour
    );

    // Count all evaluations saved (for dashboard KPI)
    @Query(value = "SELECT COUNT(*) FROM evaluation", nativeQuery = true)
    long countTotalEvaluations();

    // Per-participant stats in a session: avg score, days evaluated, absences
    @Query(value = """
        SELECT
            e.id_employe                                                        AS idEmploye,
            ROUND(AVG(ec.score)::numeric, 2)                                    AS avgScore,
            COUNT(DISTINCT e.jour)                                              AS joursEvalues,
            SUM(CASE WHEN e.presence = 'ABSENT' THEN 1 ELSE 0 END)             AS absences
        FROM evaluation e
        JOIN evaluation_critere ec ON ec.id_evaluation = e.id
        WHERE e.id_session = :sessionId
        GROUP BY e.id_employe
    """, nativeQuery = true)
    List<Object[]> getParticipantStatsForSession(@Param("sessionId") Integer sessionId);
}