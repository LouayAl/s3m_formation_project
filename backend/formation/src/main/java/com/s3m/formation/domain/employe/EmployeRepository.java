package com.s3m.formation.domain.employe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeRepository extends JpaRepository<Employe, Integer> {
    List<Employe> findByEntreprise_IdEntreprise(Integer idEntreprise);
    boolean existsByEntreprise_IdEntreprise(Integer idEntreprise);
    // Optional: find by email (unique)
    Optional<Employe> findByEmail(String email);

    boolean existsByEmail(String email);

    // Search by keyword in nom, prenom, email, matricule
    @Query("""
        SELECT e FROM Employe e
        WHERE LOWER(e.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(e.matricule) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Employe> search(@Param("keyword") String keyword);

    @Query("""
        SELECT e FROM Employe e
        WHERE (:departementId IS NULL OR e.departement.id = :departementId)
          AND (:fonction IS NULL OR e.fonction = :fonction)
          AND (:entrepriseId IS NULL OR e.entreprise.id = :entrepriseId)
        ORDER BY e.nom, e.prenom
    """)
    List<Employe> filter(
            @Param("departementId") Integer departementId,
            @Param("fonction") String fonction,
            @Param("entrepriseId") Integer entrepriseId
    );

    boolean existsByMatricule(String matricule);

}
