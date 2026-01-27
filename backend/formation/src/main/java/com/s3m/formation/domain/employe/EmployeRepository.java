package com.s3m.formation.domain.employe;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeRepository extends JpaRepository<Employe, Integer> {
    List<Employe> findByEntreprise_IdEntreprise(Integer idEntreprise);
    boolean existsByEntreprise_IdEntreprise(Integer idEntreprise);

}
