package com.s3m.formation.domain.departement;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartementRepository extends JpaRepository<Departement, Integer> {

    // Find all departments for a given entreprise
    List<Departement> findByEntrepriseIdEntreprise(Integer entrepriseId);

    // Optional: find by name and entreprise
    Departement findByNomAndEntrepriseIdEntreprise(String nom, Integer entrepriseId);
    Optional<Departement> findByNomIgnoreCase(String nom);

}
