package com.s3m.formation.domain.critereTemplate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CritereTemplateEntryRepository extends JpaRepository<CritereTemplateEntry, Integer> {

    List<CritereTemplateEntry> findByTemplate_TypeOrderByJourAscCritereIndexAsc(CritereTemplateType type);

    void deleteByTemplate_Type(CritereTemplateType type);

    List<CritereTemplateEntry> findByTemplate_IdOrderByJourAscCritereIndexAsc(Integer templateId);
    void deleteByTemplate_Id(Integer templateId);
}