package com.s3m.formation.domain.fiche;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FicheTechniqueFormationRepository
        extends JpaRepository<FicheTechniqueFormation, Integer> {

    List<FicheTechniqueFormation> findByFormation_IdFormation(Integer idFormation);

    Optional<FicheTechniqueFormation> findByFormation_IdFormationAndStatut(
            Integer idFormation,
            String statut
    );

    List<FicheTechniqueFormation>
    findByFormation_IdFormationOrderByVersionNumeroDesc(Integer idFormation);

}
