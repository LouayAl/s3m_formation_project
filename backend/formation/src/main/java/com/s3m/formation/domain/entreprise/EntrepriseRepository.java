package com.s3m.formation.domain.entreprise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EntrepriseRepository
        extends JpaRepository<Entreprise, Integer> {
    // Search entreprises by keyword (name)
    @Query("""
        SELECT e FROM Entreprise e 
        WHERE LOWER(e.nomEntreprise) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Entreprise> search(@Param("keyword") String keyword);
}
