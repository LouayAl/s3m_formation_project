package com.s3m.formation.domain.besoinFormation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BesoinFormationRepository extends JpaRepository<BesoinFormation, Integer> {
    List<BesoinFormation> findByEntreprise_IdEntreprise(Integer entrepriseId);
}