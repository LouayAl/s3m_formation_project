package com.s3m.formation.domain.critereTemplate;

import com.s3m.formation.api.dto.*;
import com.s3m.formation.domain.sessionCritere.SessionCritere;
import com.s3m.formation.domain.sessionCritere.SessionCritereRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CritereTemplateService {

    private final CritereTemplateRepository templateRepo;
    private final CritereTemplateEntryRepository entryRepo;
    private final SessionCritereRepository sessionCritereRepo;
    private final SessionFormationRepository sessionRepo;

    // ─── Template list / create / delete ───────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CritereTemplateDto> listTemplates(CritereTemplateType type) {
        List<CritereTemplate> templates = (type != null)
                ? templateRepo.findByTypeOrderByNomAsc(type)
                : templateRepo.findAllByOrderByTypeAscNomAsc();
        return templates.stream()
                .map(t -> new CritereTemplateDto(t.getId(), t.getNom(), t.getType().name()))
                .toList();
    }

    public CritereTemplateDto createTemplate(CreateCritereTemplateRequest request) {
        if (request.nom() == null || request.nom().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom du modèle est requis.");
        }
        CritereTemplateType type;
        try {
            type = CritereTemplateType.valueOf(request.type());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Type invalide.");
        }

        CritereTemplate saved = templateRepo.save(CritereTemplate.builder()
                .nom(request.nom().trim())
                .type(type)
                .build());

        return new CritereTemplateDto(saved.getId(), saved.getNom(), saved.getType().name());
    }

    public void deleteTemplate(Integer templateId) {
        if (!templateRepo.existsById(templateId)) {
            throw new EntityNotFoundException("Modèle non trouvé");
        }
        templateRepo.deleteById(templateId); // cascades to entries via FK ON DELETE CASCADE
    }

    // ─── Entries for one template ───────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CritereTemplateEntryDto> getTemplateEntries(Integer templateId) {
        return entryRepo.findByTemplate_IdOrderByJourAscCritereIndexAsc(templateId)
                .stream()
                .map(e -> new CritereTemplateEntryDto(e.getId(), e.getJour(), e.getCritereIndex(), e.getLibelle(), e.getCategorie()))
                .toList();
    }

    public List<CritereTemplateEntryDto> saveTemplateEntries(Integer templateId, CritereTemplateEntriesRequest request) {
        CritereTemplate template = templateRepo.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Modèle non trouvé"));

        List<CritereTemplateEntriesRequest.Entry> entries = request.entries().stream()
                .filter(e -> e.libelle() != null && !e.libelle().trim().isBlank())
                .toList();

        if (entries.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veuillez ajouter au moins un critère.");
        }

        entryRepo.deleteByTemplate_Id(templateId);
        entryRepo.flush();

        if (template.getType() == CritereTemplateType.UPSKILLING) {
            for (int i = 0; i < entries.size(); i++) {
                var e = entries.get(i);
                entryRepo.save(CritereTemplateEntry.builder()
                        .template(template).jour(0).critereIndex(i)
                        .libelle(e.libelle().trim())
                        .categorie(blankToNull(e.categorie()))
                        .build());
            }
        } else {
            var byJour = entries.stream()
                    .collect(Collectors.groupingBy(
                            e -> e.jour() != null ? e.jour() : 1,
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));
            for (var jourEntries : byJour.entrySet()) {
                int jour = jourEntries.getKey();
                var dayList = jourEntries.getValue();
                for (int i = 0; i < dayList.size(); i++) {
                    var e = dayList.get(i);
                    entryRepo.save(CritereTemplateEntry.builder()
                            .template(template).jour(jour).critereIndex(i)
                            .libelle(e.libelle().trim())
                            .categorie(blankToNull(e.categorie()))
                            .build());
                }
            }
        }

        return getTemplateEntries(templateId);
    }

    // ─── Clone a specific template into a real session ─────────────────────────

    public void cloneIntoSession(Integer sessionId, Integer templateId) {
        SessionFormation session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session non trouvée"));

        CritereTemplate template = templateRepo.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Modèle non trouvé"));

        int totalDays = session.getDJours() != null
                ? Math.max(1, session.getDJours().setScale(0, RoundingMode.CEILING).intValue())
                : 1;

        List<CritereTemplateEntry> templateEntries = entryRepo.findByTemplate_IdOrderByJourAscCritereIndexAsc(templateId);
        if (templateEntries.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ce modèle ne contient aucun critère. Configurez-le d'abord.");
        }

        sessionCritereRepo.deleteBySession_IdSession(sessionId);
        sessionCritereRepo.flush();

        if (template.getType() == CritereTemplateType.UPSKILLING) {
            for (int day = 1; day <= totalDays; day++) {
                for (CritereTemplateEntry te : templateEntries) {
                    sessionCritereRepo.save(SessionCritere.builder()
                            .session(session).jour(day)
                            .critereIndex(te.getCritereIndex())
                            .libelle(te.getLibelle())
                            .categorie(te.getCategorie())
                            .build());
                }
            }
        } else {
            for (CritereTemplateEntry te : templateEntries) {
                if (te.getJour() > totalDays) continue;
                sessionCritereRepo.save(SessionCritere.builder()
                        .session(session).jour(te.getJour())
                        .critereIndex(te.getCritereIndex())
                        .libelle(te.getLibelle())
                        .categorie(te.getCategorie())
                        .build());
            }
        }
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}