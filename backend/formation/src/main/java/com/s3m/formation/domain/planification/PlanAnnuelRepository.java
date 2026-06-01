// src/main/java/com/s3m/formation/domain/planification/PlanAnnuelRepository.java
package com.s3m.formation.domain.planification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanAnnuelRepository extends JpaRepository<PlanAnnuel, Integer> {

    Optional<PlanAnnuel> findByAnneeAndEntreprise_IdEntreprise(
            Integer annee, Integer entrepriseId);
}