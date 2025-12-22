package com.s3m.formation.domain.formation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface FormationRepository extends JpaRepository<Formation, Integer> {

    Optional<Formation> findByReferenceFormation(String referenceFormation);

    boolean existsByReferenceFormation(String referenceFormation);

    @Query("""
        SELECT f FROM Formation f
        WHERE LOWER(f.module) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(f.referenceFormation) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(f.familleFormation) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(f.sousFamille) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Formation> search(@Param("keyword") String keyword);

    @Query("""
        SELECT f FROM Formation f
        WHERE (:famille IS NULL OR f.familleFormation = :famille)
          AND (:type IS NULL OR f.typeFormation = :type)
          AND (:annee IS NULL OR f.annee = :annee)
    """)
    List<Formation> filter(
            @Param("famille") String famille,
            @Param("type") String type,
            @Param("annee") Integer annee
    );
}
