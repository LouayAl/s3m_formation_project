package com.s3m.formation.domain.planification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SessionPlanifieeRepository extends JpaRepository<SessionPlanifiee, Integer> {

    // All planned sessions for a given entreprise + year, ordered by date
    @Query("""
        SELECT s FROM SessionPlanifiee s
        WHERE s.entreprise.idEntreprise = :entrepriseId
          AND EXTRACT(YEAR FROM s.dateSession) = :annee
        ORDER BY s.dateSession ASC
    """)
    List<SessionPlanifiee> findByEntrepriseAndYear(
            @Param("entrepriseId") Integer entrepriseId,
            @Param("annee") int annee
    );

    // Verify ownership before update/delete
    boolean existsByIdAndEntreprise_IdEntreprise(Integer id, Integer entrepriseId);

    void deleteAllByEntreprise_IdEntrepriseAndDateSessionBetween(
            Integer entrepriseId, LocalDate start, LocalDate end);
}