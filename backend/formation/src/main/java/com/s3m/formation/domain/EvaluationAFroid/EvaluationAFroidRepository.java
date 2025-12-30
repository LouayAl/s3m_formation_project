package com.s3m.formation.domain.EvaluationAFroid;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationAFroidRepository extends JpaRepository<EvaluationAFroid, Integer> {
    List<EvaluationAFroid> findByParticipation_Session_Demande_Entreprise_IdEntreprise(Integer clientId);
}

