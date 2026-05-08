package com.s3m.formation.domain.formateur;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormateurRepository
        extends JpaRepository<Formateur, Integer> {

    List<Formateur> findByActifTrue();
    List<Formateur> findByEntreprise_IdEntreprise(Integer entrepriseId);
    Optional<Formateur> findByEmail(String email);

}
