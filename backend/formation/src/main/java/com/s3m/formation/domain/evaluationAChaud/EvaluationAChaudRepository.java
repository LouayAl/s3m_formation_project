package com.s3m.formation.domain.evaluationAChaud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EvaluationAChaudRepository extends JpaRepository<EvaluationAChaud, Integer> {

    boolean existsBySession_IdSessionAndEmploye_IdEmploye(
            Integer sessionId, Integer employeId);

    List<EvaluationAChaud> findBySession_IdSession(Integer sessionId);

    // ── KPI queries ───────────────────────────────────────────────────────────

    @Query("SELECT AVG(r.score) FROM EvaluationReponse r")
    Optional<Double> findAvgScoreGlobalS3M();

    @Query("SELECT COUNT(r) FROM EvaluationReponse r")
    int countReponsesGlobalS3M();

    @Query("""
        SELECT AVG(r.score)
        FROM EvaluationReponse r
        JOIN r.evalChaud e
        JOIN e.session s
        WHERE s.entreprise.idEntreprise = :entrepriseId
    """)
    Optional<Double> findAvgScoreByEntreprise(@Param("entrepriseId") Integer entrepriseId);

    @Query("""
        SELECT COUNT(r)
        FROM EvaluationReponse r
        JOIN r.evalChaud e
        JOIN e.session s
        WHERE s.entreprise.idEntreprise = :entrepriseId
    """)
    int countReponsesForEntreprise(@Param("entrepriseId") Integer entrepriseId);

    @Query("""
        SELECT AVG(r.score)
        FROM EvaluationReponse r
        JOIN r.evalChaud e
        JOIN e.session s
        WHERE s.entreprise.idEntreprise = :entrepriseId
          AND s.formation.idFormation = :formationId
    """)
    Optional<Double> findAvgScoreByEntrepriseAndFormation(
            @Param("entrepriseId") Integer entrepriseId,
            @Param("formationId") Integer formationId);

    @Query("""
        SELECT COUNT(r)
        FROM EvaluationReponse r
        JOIN r.evalChaud e
        JOIN e.session s
        WHERE s.entreprise.idEntreprise = :entrepriseId
          AND s.formation.idFormation = :formationId
    """)
    int countReponsesForEntrepriseAndFormation(
            @Param("entrepriseId") Integer entrepriseId,
            @Param("formationId") Integer formationId);

    @Query("""
        SELECT AVG(r.score)
        FROM EvaluationReponse r
        JOIN r.evalChaud e
        WHERE e.session.idSession = :sessionId
    """)
    Optional<Double> findAvgScoreBySession(@Param("sessionId") Integer sessionId);

    @Query("""
        SELECT COUNT(r)
        FROM EvaluationReponse r
        JOIN r.evalChaud e
        WHERE e.session.idSession = :sessionId
    """)
    int countReponsesForSession(@Param("sessionId") Integer sessionId);
}