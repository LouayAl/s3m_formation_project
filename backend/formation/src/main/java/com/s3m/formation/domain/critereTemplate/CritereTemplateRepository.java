package com.s3m.formation.domain.critereTemplate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CritereTemplateRepository extends JpaRepository<CritereTemplate, Integer> {
    List<CritereTemplate> findByTypeOrderByNomAsc(CritereTemplateType type);
    List<CritereTemplate> findAllByOrderByTypeAscNomAsc();
}