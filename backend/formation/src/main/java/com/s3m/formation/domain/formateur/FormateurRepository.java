package com.s3m.formation.domain.formateur;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormateurRepository
        extends JpaRepository<Formateur, Integer> {
    /**
     * Find only active formateurs
     */
    List<Formateur> findByActifTrue();

    /**
     * Optional: find active formateur by id
     */

    /**
     * Optional: find formateurs by fournisseur (Entreprise)
     */
    List<Formateur> findByEntreprise_IdEntreprise(Integer entrepriseId);
}
