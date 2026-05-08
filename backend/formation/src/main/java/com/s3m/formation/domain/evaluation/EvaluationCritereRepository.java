package com.s3m.formation.domain.evaluation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationCritereRepository extends JpaRepository<EvaluationCritere, Integer> {

    void deleteByEvaluation_Session_IdSessionAndCritereIndexGreaterThanEqual(
            Integer idSession,
            Integer critereIndex
    );
}
