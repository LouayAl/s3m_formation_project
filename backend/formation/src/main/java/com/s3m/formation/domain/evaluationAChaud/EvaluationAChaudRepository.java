package com.s3m.formation.domain.evaluationAChaud;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface EvaluationAChaudRepository extends JpaRepository<EvaluationAChaud, Integer> {

    List<EvaluationAChaud> findBySession_IdSession(Integer sessionId);

    List<EvaluationAChaud> findBySession_IdSessionAndJourEvaluation(
            Integer sessionId, LocalDate jour);

    boolean existsBySession_IdSessionAndEmploye_IdEmployeAndJourEvaluation(
            Integer sessionId, Integer employeId, LocalDate jour);
}