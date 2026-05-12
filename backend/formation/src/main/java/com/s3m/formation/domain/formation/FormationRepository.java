package com.s3m.formation.domain.formation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FormationRepository extends JpaRepository<Formation, Integer> {

    Optional<Formation> findByReferenceFormation(String referenceFormation);

    boolean existsByReferenceFormation(String referenceFormation);

    // =========================
    // SCOPED SEARCH
    // =========================
    @Query("""
        SELECT f FROM Formation f
        WHERE f.entreprise.idEntreprise = :entrepriseId
          AND (
            LOWER(f.module) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(f.referenceFormation) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(f.familleFormation) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(f.sousFamille) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    """)
    List<Formation> search(
            @Param("keyword") String keyword,
            @Param("entrepriseId") Integer entrepriseId
    );

    // =========================
    // SCOPED FILTER
    // =========================
    @Query("""
        SELECT f FROM Formation f
        WHERE f.entreprise.idEntreprise = :entrepriseId
          AND (:famille IS NULL OR f.familleFormation = :famille)
          AND (:type IS NULL OR f.typeFormation = :type)
          AND (:annee IS NULL OR f.annee = :annee)
    """)
    List<Formation> filter(
            @Param("famille") String famille,
            @Param("type") String type,
            @Param("annee") Integer annee,
            @Param("entrepriseId") Integer entrepriseId
    );

    // =========================
    // ALTERNATE SEARCH (ADVANCED FILTER)
    // =========================
    @Query("""
        SELECT f FROM Formation f
        WHERE f.entreprise.idEntreprise = :entrepriseId
          AND (:module IS NULL OR LOWER(f.module) LIKE LOWER(CONCAT('%', :module, '%')))
          AND (:typeFormation IS NULL OR f.typeFormation = :typeFormation)
          AND (:famille IS NULL OR f.familleFormation = :famille)
          AND (:sousFamille IS NULL OR f.sousFamille = :sousFamille)
        ORDER BY f.module
    """)
    List<Formation> searchAdvanced(
            @Param("module") String module,
            @Param("typeFormation") String typeFormation,
            @Param("famille") String famille,
            @Param("sousFamille") String sousFamille,
            @Param("entrepriseId") Integer entrepriseId
    );

    // =========================
    // SIMPLE SCOPED LIST
    // =========================
    List<Formation> findByEntreprise_IdEntreprise(Integer entrepriseId);


}