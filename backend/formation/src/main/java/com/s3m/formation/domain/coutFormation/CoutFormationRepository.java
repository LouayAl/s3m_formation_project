package com.s3m.formation.domain.coutFormation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoutFormationRepository extends JpaRepository<CoutFormation, Integer> {
    List<CoutFormation> findBySession_Formation_IdFormation(Integer formationId);
    List<CoutFormation> findBySession_Demande_Entreprise_IdEntreprise(Integer clientId); // fixed
    List<CoutFormation> findBySession_IdSession(Integer sessionId);
}

