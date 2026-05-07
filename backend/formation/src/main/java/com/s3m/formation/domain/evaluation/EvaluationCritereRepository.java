package com.s3m.formation.domain.evaluation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationCritereRepository extends JpaRepository<EvaluationCritere, Integer> {
    // All operations go through the Evaluation cascade — no extra methods needed here
}