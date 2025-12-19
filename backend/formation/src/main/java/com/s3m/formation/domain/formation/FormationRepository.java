package com.s3m.formation.domain.formation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormationRepository extends JpaRepository<Formation, Integer> {

    Optional<Formation> findByReferenceFormation(String referenceFormation);

    boolean existsByReferenceFormation(String referenceFormation);

}
